package com.mindskip.xzs.controller.student;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.Video;
import com.mindskip.xzs.domain.VideoChapter;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.VideoChapterService;
import com.mindskip.xzs.service.VideoService;
import com.mindskip.xzs.viewmodel.admin.video.VideoChapterResponseVM;
import com.mindskip.xzs.viewmodel.admin.video.VideoResponseVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("StudentVideoController")
@RequestMapping(value = "/api/student/video")
public class VideoController extends BaseApiController {

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

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse<List<VideoResponseVM>> list() {
        List<Video> videos = videoService.allVideo();
        List<VideoResponseVM> vmList = videos.stream().map(e -> {
            VideoResponseVM vm = modelMapper.map(e, VideoResponseVM.class);
            if (e.getSubjectId() != null) {
                Subject subject = subjectService.selectById(e.getSubjectId());
                if (subject != null) {
                    vm.setSubjectName(subject.getName());
                }
            }
            return vm;
        }).collect(Collectors.toList());
        return RestResponse.ok(vmList);
    }

    @RequestMapping(value = "/listBySubject/{subjectId}", method = RequestMethod.POST)
    public RestResponse<List<VideoResponseVM>> listBySubject(@PathVariable Integer subjectId) {
        List<Video> videos = videoService.getVideoBySubjectId(subjectId);
        List<VideoResponseVM> vmList = videos.stream().map(e -> {
            VideoResponseVM vm = modelMapper.map(e, VideoResponseVM.class);
            if (e.getSubjectId() != null) {
                Subject subject = subjectService.selectById(e.getSubjectId());
                if (subject != null) {
                    vm.setSubjectName(subject.getName());
                }
            }
            return vm;
        }).collect(Collectors.toList());
        return RestResponse.ok(vmList);
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
}
