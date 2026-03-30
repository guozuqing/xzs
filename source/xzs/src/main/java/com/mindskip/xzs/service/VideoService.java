package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.Video;
import com.mindskip.xzs.viewmodel.admin.video.VideoPageRequestVM;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface VideoService extends BaseService<Video> {

    PageInfo<Video> page(VideoPageRequestVM requestVM);

    List<Video> allVideo();

    List<Video> getVideoBySubjectId(Integer subjectId);
}
