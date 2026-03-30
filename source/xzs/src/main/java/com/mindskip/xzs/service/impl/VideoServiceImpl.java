package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.domain.Video;
import com.mindskip.xzs.repository.VideoMapper;
import com.mindskip.xzs.service.VideoService;
import com.mindskip.xzs.viewmodel.admin.video.VideoPageRequestVM;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl extends BaseServiceImpl<Video> implements VideoService {

    private final VideoMapper videoMapper;

    @Autowired
    public VideoServiceImpl(VideoMapper videoMapper) {
        super(videoMapper);
        this.videoMapper = videoMapper;
    }

    @Override
    public PageInfo<Video> page(VideoPageRequestVM requestVM) {
        return PageHelper.startPage(requestVM.getPageIndex(), requestVM.getPageSize(), "item_order, id desc").doSelectPageInfo(() ->
                videoMapper.page(requestVM)
        );
    }

    @Override
    public List<Video> allVideo() {
        return videoMapper.allVideo();
    }

    @Override
    public List<Video> getVideoBySubjectId(Integer subjectId) {
        return videoMapper.getVideoBySubjectId(subjectId);
    }
}
