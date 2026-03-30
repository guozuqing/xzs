package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<ClassInfo> {

    List<ClassInfo> page(@Param("name") String name);

    List<ClassInfo> allClasses();
}
