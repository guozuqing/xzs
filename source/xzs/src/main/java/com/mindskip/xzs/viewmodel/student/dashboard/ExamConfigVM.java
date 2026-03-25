package com.mindskip.xzs.viewmodel.student.dashboard;

import java.util.List;

public class ExamConfigVM {
    private String userTypeName;
    private Integer userLevel;
    private String realName;
    private String userName;
    private List<ExamItemVM> examItems;

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<ExamItemVM> getExamItems() {
        return examItems;
    }

    public void setExamItems(List<ExamItemVM> examItems) {
        this.examItems = examItems;
    }
}
