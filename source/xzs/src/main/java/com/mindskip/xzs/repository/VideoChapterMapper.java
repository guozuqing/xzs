package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.VideoChapter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoChapterMapper extends BaseMapper<VideoChapter> {

    List<VideoChapter> getChaptersByVideoId(Integer videoId);

    int deleteByVideoId(Integer videoId);
}
