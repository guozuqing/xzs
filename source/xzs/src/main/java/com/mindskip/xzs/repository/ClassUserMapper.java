package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.ClassUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassUserMapper extends BaseMapper<ClassUser> {

    List<ClassUser> selectByClassId(@Param("classId") Integer classId);

    int deleteByClassIdAndUserId(@Param("classId") Integer classId, @Param("userId") Integer userId);

    int deleteByClassId(@Param("classId") Integer classId);
}
