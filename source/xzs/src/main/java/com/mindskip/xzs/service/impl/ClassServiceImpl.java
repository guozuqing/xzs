package com.mindskip.xzs.service.impl;

import com.mindskip.xzs.domain.ClassInfo;
import com.mindskip.xzs.repository.ClassMapper;
import com.mindskip.xzs.service.ClassService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl extends BaseServiceImpl<ClassInfo> implements ClassService {

    private final ClassMapper classMapper;

    @Autowired
    public ClassServiceImpl(ClassMapper classMapper) {
        super(classMapper);
        this.classMapper = classMapper;
    }

    @Override
    public PageInfo<ClassInfo> page(String name, Integer pageIndex, Integer pageSize) {
        return PageHelper.startPage(pageIndex, pageSize, "id desc").doSelectPageInfo(() ->
                classMapper.page(name)
        );
    }

    @Override
    public List<ClassInfo> allClasses() {
        return classMapper.allClasses();
    }
}
