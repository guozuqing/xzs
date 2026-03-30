package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.VideoChapter;

import java.util.List;

public interface VideoChapterService extends BaseService<VideoChapter> {

    List<VideoChapter> getChaptersByVideoId(Integer videoId);

    int deleteByVideoId(Integer videoId);
}
