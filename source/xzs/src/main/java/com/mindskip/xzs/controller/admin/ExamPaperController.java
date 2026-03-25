package com.mindskip.xzs.controller.admin;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.ExamConfig;
import com.mindskip.xzs.repository.ExamConfigMapper;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.ExamUtil;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperPublishRequestVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController("AdminExamPaperController")
@RequestMapping(value = "/api/admin/exam/paper")
public class ExamPaperController extends BaseApiController {

    private final ExamConfigMapper examConfigMapper;

    @Autowired
    public ExamPaperController(ExamConfigMapper examConfigMapper) {
        this.examConfigMapper = examConfigMapper;
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public RestResponse<List<Map<String, Object>>> list() {
        List<ExamConfig> configs = examConfigMapper.selectAllActive();
        List<Map<String, Object>> result = configs.stream().map(c -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("subjectId", c.getSubjectId());
            map.put("questionCount", c.getQuestionCount());
            map.put("score", c.getScore());
            map.put("suggestTime", c.getSuggestTime());
            map.put("passScore", c.getPassScore());
            map.put("createTime", DateTimeUtil.dateFormat(c.getCreateTime()));
            return map;
        }).collect(Collectors.toList());
        return RestResponse.ok(result);
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST)
    public RestResponse publish(@RequestBody @Valid ExamPaperPublishRequestVM model) {
        Integer scorePerQuestion = ExamUtil.scoreFromVM(model.getScorePerQuestion());
        int totalScore = model.getQuestionCount() * scorePerQuestion;

        ExamConfig config = new ExamConfig();
        config.setName(model.getName());
        config.setSubjectId(model.getSubjectId());
        config.setQuestionCount(model.getQuestionCount());
        config.setScore(totalScore);
        config.setSuggestTime(model.getSuggestTime());
        config.setPassScore(ExamUtil.scoreFromVM(model.getPassScore()));
        config.setCreateUser(getCurrentUser().getId());
        config.setCreateTime(new Date());
        config.setDeleted(false);
        examConfigMapper.insertSelective(config);
        return RestResponse.ok();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public RestResponse delete(@PathVariable Integer id) {
        examConfigMapper.deleteById(id);
        return RestResponse.ok();
    }
}
