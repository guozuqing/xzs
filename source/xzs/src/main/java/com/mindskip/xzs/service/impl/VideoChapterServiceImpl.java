package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.domain.VideoChapter;
import com.mindskip.xzs.repository.VideoChapterMapper;
import com.mindskip.xzs.service.VideoChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoChapterServiceImpl extends BaseServiceImpl<VideoChapter> implements VideoChapterService {

    private final VideoChapterMapper videoChapterMapper;

    @Autowired
    public VideoChapterServiceImpl(VideoChapterMapper videoChapterMapper) {
        super(videoChapterMapper);
        this.videoChapterMapper = videoChapterMapper;
    }

    @Override
    public List<VideoChapter> getChaptersByVideoId(Integer videoId) {
        return videoChapterMapper.getChaptersByVideoId(videoId);
    }

    @Override
    public int deleteByVideoId(Integer videoId) {
        return videoChapterMapper.deleteByVideoId(videoId);
    }
}
