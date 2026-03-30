package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.Video;
import com.mindskip.xzs.viewmodel.admin.video.VideoPageRequestVM;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {

    List<Video> page(VideoPageRequestVM requestVM);

    List<Video> allVideo();

    List<Video> getVideoBySubjectId(Integer subjectId);
}
