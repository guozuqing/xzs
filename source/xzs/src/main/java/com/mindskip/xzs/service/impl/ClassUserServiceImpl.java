package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.domain.ClassUser;
import com.mindskip.xzs.repository.ClassUserMapper;
import com.mindskip.xzs.service.ClassUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassUserServiceImpl extends BaseServiceImpl<ClassUser> implements ClassUserService {

    private final ClassUserMapper classUserMapper;

    @Autowired
    public ClassUserServiceImpl(ClassUserMapper classUserMapper) {
        super(classUserMapper);
        this.classUserMapper = classUserMapper;
    }

    @Override
    public List<ClassUser> selectByClassId(Integer classId) {
        return classUserMapper.selectByClassId(classId);
    }

    @Override
    public int deleteByClassIdAndUserId(Integer classId, Integer userId) {
        return classUserMapper.deleteByClassIdAndUserId(classId, userId);
    }

    @Override
    public int deleteByClassId(Integer classId) {
        return classUserMapper.deleteByClassId(classId);
    }
}
