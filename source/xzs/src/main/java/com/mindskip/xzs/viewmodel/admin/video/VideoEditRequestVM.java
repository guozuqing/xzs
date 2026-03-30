package com.mindskip.xzs.viewmodel.admin.video;

import com.mindskip.xzs.viewmodel.BaseVM;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class VideoEditRequestVM extends BaseVM {

    private Integer id;

    @NotBlank
    private String name;

    private Integer subjectId;

    private String videoUrl;

    private String coverUrl;

    private String description;

    private Integer gradeLevel;

    private Integer itemOrder;

    private List<VideoChapterRequestVM> chapters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public List<VideoChapterRequestVM> getChapters() {
        return chapters;
    }

    public void setChapters(List<VideoChapterRequestVM> chapters) {
        this.chapters = chapters;
    }
}
