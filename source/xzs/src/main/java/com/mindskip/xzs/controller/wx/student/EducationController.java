package com.mindskip.xzs.controller.wx.student;

import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.controller.wx.BaseWXApiController;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("WXStudentEducationController")
@RequestMapping(value = "/api/wx/student/education")
public class EducationController extends BaseWXApiController {

    private final SubjectService subjectService;

    @Autowired
    public EducationController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @RequestMapping(value = "/subject/list", method = RequestMethod.POST)
    public RestResponse<List<Subject>> list() {
        List<Subject> subjects = subjectService.allSubject();
        return RestResponse.ok(subjects);
    }
}
