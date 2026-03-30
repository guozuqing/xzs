package com.mindskip.xzs.viewmodel.admin.video;

import com.mindskip.xzs.base.BasePage;

public class VideoPageRequestVM extends BasePage {
    private Integer id;
    private Integer subjectId;
    private Integer gradeLevel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
}
