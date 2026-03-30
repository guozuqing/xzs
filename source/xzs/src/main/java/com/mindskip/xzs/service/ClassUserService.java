package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.ClassUser;

import java.util.List;

public interface ClassUserService extends BaseService<ClassUser> {

    List<ClassUser> selectByClassId(Integer classId);

    int deleteByClassIdAndUserId(Integer classId, Integer userId);

    int deleteByClassId(Integer classId);
}
