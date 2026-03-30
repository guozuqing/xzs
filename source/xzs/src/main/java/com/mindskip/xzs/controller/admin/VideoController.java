package com.mindskip.xzs.controller.admin;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.Video;
import com.mindskip.xzs.domain.VideoChapter;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.VideoChapterService;
import com.mindskip.xzs.service.VideoService;
import com.mindskip.xzs.utility.PageInfoHelper;
import com.mindskip.xzs.viewmodel.admin.video.*;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController("AdminVideoController")
@RequestMapping(value = "/api/admin/video")
public class VideoController extends BaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
    private static final String UPLOAD_DIR = "/home/skydomain/upload/video/";

    private final VideoService videoService;
    private final VideoChapterService videoChapterService;
    private final SubjectService subjectService;

    @Autowired
    public VideoController(VideoService videoService, VideoChapterService videoChapterService,
                           SubjectService subjectService) {
        this.videoService = videoService;
        this.videoChapterService = videoChapterService;
        this.subjectService = subjectService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<VideoResponseVM>> pageList(@RequestBody VideoPageRequestVM model) {
        PageInfo<Video> pageInfo = videoService.page(model);
        PageInfo<VideoResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            VideoResponseVM vm = modelMapper.map(e, VideoResponseVM.class);
            if (e.getSubjectId() != null) {
                Subject subject = subjectService.selectById(e.getSubjectId());
                if (subject != null) {
                    vm.setSubjectName(subject.getName());
                }
            }
            return vm;
        });
        return RestResponse.ok(page);
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public RestResponse edit(@RequestBody @Valid VideoEditRequestVM model) {
        Video video = modelMapper.map(model, Video.class);
        if (model.getId() == null) {
            video.setCreateTime(new Date());
            video.setCreateUser(getCurrentUser().getId());
            video.setDeleted(false);
            videoService.insertByFilter(video);
        } else {
            videoService.updateByIdFilter(video);
            videoChapterService.deleteByVideoId(video.getId());
        }
        if (model.getChapters() != null) {
            int order = 1;
            for (VideoChapterRequestVM chapterVM : model.getChapters()) {
                VideoChapter chapter = new VideoChapter();
                chapter.setVideoId(video.getId());
                chapter.setTitle(chapterVM.getTitle());
                chapter.setVideoUrl(chapterVM.getVideoUrl());
                chapter.setDuration(chapterVM.getDuration());
                chapter.setItemOrder(chapterVM.getItemOrder() != null ? chapterVM.getItemOrder() : order);
                chapter.setCreateTime(new Date());
                chapter.setDeleted(false);
                videoChapterService.insertByFilter(chapter);
                order++;
            }
        }
        return RestResponse.ok();
    }

    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<VideoResponseVM> select(@PathVariable Integer id) {
        Video video = videoService.selectById(id);
        VideoResponseVM vm = modelMapper.map(video, VideoResponseVM.class);
        if (video.getSubjectId() != null) {
            Subject subject = subjectService.selectById(video.getSubjectId());
            if (subject != null) {
                vm.setSubjectName(subject.getName());
            }
        }
        List<VideoChapter> chapters = videoChapterService.getChaptersByVideoId(id);
        List<VideoChapterResponseVM> chapterVMs = chapters.stream()
                .map(c -> modelMapper.map(c, VideoChapterResponseVM.class))
                .collect(Collectors.toList());
        vm.setChapters(chapterVMs);
        return RestResponse.ok(vm);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        Video video = videoService.selectById(id);
        video.setDeleted(true);
        videoService.updateByIdFilter(video);
        videoChapterService.deleteByVideoId(id);
        return RestResponse.ok();
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse uploadVideo(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        String originalName = multipartFile.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        String baseName = UUID.randomUUID().toString().replace("-", "");
        String newFileName = baseName + ext;
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(UPLOAD_DIR + newFileName);
            multipartFile.transferTo(dest);

            boolean isVideo = ext.equals(".mp4") || ext.equals(".mov") || ext.equals(".avi")
                    || ext.equals(".mkv") || ext.equals(".webm") || ext.equals(".flv");
            if (isVideo) {
                String finalFileName = transcodeToH264IfNeeded(dest, baseName);
                String filePath = "/api/video/file/" + finalFileName;
                return RestResponse.ok(filePath);
            }

            String filePath = "/api/video/file/" + newFileName;
            return RestResponse.ok(filePath);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return RestResponse.fail(2, e.getMessage());
        }
    }

    private String transcodeToH264IfNeeded(File videoFile, String baseName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffprobe", "-v", "quiet",
                    "-select_streams", "v:0", "-show_entries", "stream=codec_name",
                    "-of", "csv=p=0", videoFile.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();
            String codec = readStream(process.getInputStream()).trim();
            process.waitFor();

            if ("h264".equalsIgnoreCase(codec)) {
                return videoFile.getName();
            }

            logger.info("视频编码为{}，开始转码为H.264...", codec);
            String outputFileName = baseName + "_h264.mp4";
            File outputFile = new File(UPLOAD_DIR + outputFileName);
            ProcessBuilder ffmpeg = new ProcessBuilder("ffmpeg", "-i", videoFile.getAbsolutePath(),
                    "-c:v", "libx264", "-preset", "fast", "-crf", "23",
                    "-c:a", "aac", "-b:a", "128k",
                    "-movflags", "+faststart",
                    "-y", outputFile.getAbsolutePath());
            ffmpeg.redirectErrorStream(true);
            Process ffmpegProcess = ffmpeg.start();
            new Thread(() -> {
                try { readStream(ffmpegProcess.getInputStream()); } catch (Exception ignored) {}
            }).start();
            int exitCode = ffmpegProcess.waitFor();

            if (exitCode == 0 && outputFile.exists()) {
                videoFile.delete();
                logger.info("视频转码完成: {}", outputFileName);
                return outputFileName;
            } else {
                logger.warn("视频转码失败，使用原文件");
                return videoFile.getName();
            }
        } catch (Exception e) {
            logger.error("视频转码异常", e);
            return videoFile.getName();
        }
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, len));
        }
        return sb.toString();
    }
}
