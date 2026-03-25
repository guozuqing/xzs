package com.mindskip.xzs.viewmodel.admin.exam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ExamPaperPublishRequestVM {

    @NotNull
    private Integer subjectId;

    @NotNull
    private Integer questionCount;

    @NotBlank
    private String name;

    @NotNull
    private Integer suggestTime;

    @NotBlank
    private String scorePerQuestion;

    @NotBlank
    private String passScore;

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSuggestTime() {
        return suggestTime;
    }

    public void setSuggestTime(Integer suggestTime) {
        this.suggestTime = suggestTime;
    }

    public String getScorePerQuestion() {
        return scorePerQuestion;
    }

    public void setScorePerQuestion(String scorePerQuestion) {
        this.scorePerQuestion = scorePerQuestion;
    }

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }
}
