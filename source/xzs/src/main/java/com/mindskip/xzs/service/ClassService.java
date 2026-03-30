package com.mindskip.xzs.service;

import com.mindskip.xzs.domain.ClassInfo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ClassService extends BaseService<ClassInfo> {

    PageInfo<ClassInfo> page(String name, Integer pageIndex, Integer pageSize);

    List<ClassInfo> allClasses();
}
