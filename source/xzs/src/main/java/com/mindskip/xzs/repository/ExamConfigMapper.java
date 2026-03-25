package com.mindskip.xzs.repository;

import com.mindskip.xzs.domain.ExamConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamConfigMapper {

    int insertSelective(ExamConfig record);

    List<ExamConfig> selectAllActive();

    ExamConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExamConfig record);

    int deleteById(Integer id);
}
