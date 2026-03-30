package com.mindskip.xzs.controller.wx.student;

import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.controller.wx.BaseWXApiController;
import com.mindskip.xzs.domain.*;
import com.mindskip.xzs.domain.enums.ExamPaperTypeEnum;
import com.mindskip.xzs.domain.task.TaskItemAnswerObject;
import com.mindskip.xzs.domain.task.TaskItemObject;
import com.mindskip.xzs.repository.ExamConfigMapper;
import com.mindskip.xzs.repository.ExamPaperAnswerMapper;
import com.mindskip.xzs.service.ExamPaperService;
import com.mindskip.xzs.service.TaskExamCustomerAnswerService;
import com.mindskip.xzs.service.TaskExamService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.DateTimeUtil;
import com.mindskip.xzs.utility.ExamUtil;
import com.mindskip.xzs.utility.JsonUtil;
import com.mindskip.xzs.viewmodel.admin.exam.ExamPaperEditRequestVM;
import com.mindskip.xzs.viewmodel.student.dashboard.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Controller("WXStudentDashboardController")
@RequestMapping(value = "/api/wx/student/dashboard")
@ResponseBody
public class DashboardController extends BaseWXApiController {

    private final ExamPaperService examPaperService;
    private final TextContentService textContentService;
    private final TaskExamService taskExamService;
    private final TaskExamCustomerAnswerService taskExamCustomerAnswerService;
    private final ExamConfigMapper examConfigMapper;
    private final ExamPaperAnswerMapper examPaperAnswerMapper;

    @Autowired
    public DashboardController(ExamPaperService examPaperService, TextContentService textContentService, TaskExamService taskExamService, TaskExamCustomerAnswerService taskExamCustomerAnswerService, ExamConfigMapper examConfigMapper, ExamPaperAnswerMapper examPaperAnswerMapper) {
        this.examPaperService = examPaperService;
        this.textContentService = textContentService;
        this.taskExamService = taskExamService;
        this.taskExamCustomerAnswerService = taskExamCustomerAnswerService;
        this.examConfigMapper = examConfigMapper;
        this.examPaperAnswerMapper = examPaperAnswerMapper;
    }

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public RestResponse<IndexVM> index() {
        IndexVM indexVM = new IndexVM();
        User user = getCurrentUser();

        PaperFilter fixedPaperFilter = new PaperFilter();
        fixedPaperFilter.setExamPaperType(ExamPaperTypeEnum.Fixed.getCode());
        indexVM.setFixedPaper(examPaperService.indexPaper(fixedPaperFilter));

        PaperFilter timeLimitPaperFilter = new PaperFilter();
        timeLimitPaperFilter.setDateTime(new Date());
        timeLimitPaperFilter.setExamPaperType(ExamPaperTypeEnum.TimeLimit.getCode());

        List<PaperInfo> limitPaper = examPaperService.indexPaper(timeLimitPaperFilter);
        List<PaperInfoVM> paperInfoVMS = limitPaper.stream().map(d -> {
            PaperInfoVM vm = modelMapper.map(d, PaperInfoVM.class);
            vm.setStartTime(DateTimeUtil.dateFormat(d.getLimitStartTime()));
            vm.setEndTime(DateTimeUtil.dateFormat(d.getLimitEndTime()));
            return vm;
        }).collect(Collectors.toList());
        indexVM.setTimeLimitPaper(paperInfoVMS);
        return RestResponse.ok(indexVM);
    }

    @RequestMapping(value = "/examConfig", method = RequestMethod.POST)
    public RestResponse<ExamConfigVM> examConfig() {
        User user = getCurrentUser();
        ExamConfigVM config = new ExamConfigVM();
        config.setRealName(user.getRealName());
        config.setUserName(user.getUserName());
        config.setUserLevel(user.getUserLevel());
        config.setUserTypeName("");

        List<ExamConfig> configs = examConfigMapper.selectAllActive();
        List<ExamItemVM> examItems = configs.stream().map(cfg -> {
            ExamItemVM item = new ExamItemVM();
            item.setExamName(cfg.getName());
            item.setExamType("theory");
            item.setSubjectId(cfg.getSubjectId());
            item.setQuestionCount(cfg.getQuestionCount());
            item.setSuggestTime(cfg.getSuggestTime());
            item.setTotalScore(ExamUtil.scoreToVM(cfg.getScore()));
            int scorePerQ = cfg.getScore() / cfg.getQuestionCount();
            item.setScorePerQuestion(ExamUtil.scoreToVM(scorePerQ));
            item.setPassScore(ExamUtil.scoreToVM(cfg.getPassScore() != null ? cfg.getPassScore() : (int)(cfg.getScore() * 0.8)));
            fillLastExamInfo(item, user.getId(), cfg.getSubjectId());
            return item;
        }).collect(Collectors.toList());

        config.setExamItems(examItems);
        return RestResponse.ok(config);
    }

    private void fillLastExamInfo(ExamItemVM item, Integer userId, Integer subjectId) {
        ExamPaperAnswer lastAnswer = examPaperAnswerMapper.getLastByUserAndSubject(userId, subjectId);
        if (lastAnswer != null) {
            item.setLastScore(ExamUtil.scoreToVM(lastAnswer.getUserScore()));
            int passScoreInt = (int) (Float.parseFloat(item.getPassScore()) * 10);
            if (lastAnswer.getUserScore() >= passScoreInt) {
                item.setLastResult("通过");
            } else {
                item.setLastResult("未通过");
            }
            item.setLastTime(DateTimeUtil.dateFormat(lastAnswer.getCreateTime()));
        }
    }

    @RequestMapping(value = "/generatePaper", method = RequestMethod.POST)
    public RestResponse<ExamPaperEditRequestVM> generatePaper(@RequestBody ExamItemVM examItemVM) {
        User user = getCurrentUser();
        Integer scorePerQuestion = ExamUtil.scoreFromVM(examItemVM.getScorePerQuestion());
        ExamPaperEditRequestVM paper = examPaperService.generateRandomPaper(
                examItemVM.getSubjectId(),
                examItemVM.getQuestionCount(),
                scorePerQuestion,
                examItemVM.getSuggestTime(),
                examItemVM.getExamName(),
                user
        );
        if (paper == null) {
            return RestResponse.fail(2, "题库中没有足够的题目");
        }
        return RestResponse.ok(paper);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    public RestResponse<List<TaskItemVm>> task() {
        User user = getCurrentUser();
        List<TaskExam> taskExams = taskExamService.getByGradeLevel(user.getUserLevel());
        if (taskExams.size() == 0) {
            return RestResponse.ok(new ArrayList<>());
        }
        List<Integer> tIds = taskExams.stream().map(taskExam -> taskExam.getId()).collect(Collectors.toList());
        List<TaskExamCustomerAnswer> taskExamCustomerAnswers = taskExamCustomerAnswerService.selectByTUid(tIds, user.getId());
        List<TaskItemVm> vm = taskExams.stream().map(t -> {
            TaskItemVm itemVm = new TaskItemVm();
            itemVm.setId(t.getId());
            itemVm.setTitle(t.getTitle());
            TaskExamCustomerAnswer taskExamCustomerAnswer = taskExamCustomerAnswers.stream()
                    .filter(tc -> tc.getTaskExamId().equals(t.getId())).findFirst().orElse(null);
            List<TaskItemPaperVm> paperItemVMS = getTaskItemPaperVm(t.getFrameTextContentId(), taskExamCustomerAnswer);
            itemVm.setPaperItems(paperItemVMS);
            return itemVm;
        }).collect(Collectors.toList());
        return RestResponse.ok(vm);
    }


    private List<TaskItemPaperVm> getTaskItemPaperVm(Integer tFrameId, TaskExamCustomerAnswer taskExamCustomerAnswers) {
        TextContent textContent = textContentService.selectById(tFrameId);
        List<TaskItemObject> paperItems = JsonUtil.toJsonListObject(textContent.getContent(), TaskItemObject.class);

        List<TaskItemAnswerObject> answerPaperItems = null;
        if (null != taskExamCustomerAnswers) {
            TextContent answerTextContent = textContentService.selectById(taskExamCustomerAnswers.getTextContentId());
            answerPaperItems = JsonUtil.toJsonListObject(answerTextContent.getContent(), TaskItemAnswerObject.class);
        }


        List<TaskItemAnswerObject> finalAnswerPaperItems = answerPaperItems;
        return paperItems.stream().map(p -> {
                    TaskItemPaperVm ivm = new TaskItemPaperVm();
                    ivm.setExamPaperId(p.getExamPaperId());
                    ivm.setExamPaperName(p.getExamPaperName());
                    if (null != finalAnswerPaperItems) {
                        finalAnswerPaperItems.stream()
                                .filter(a -> a.getExamPaperId().equals(p.getExamPaperId()))
                                .findFirst()
                                .ifPresent(a -> {
                                    ivm.setExamPaperAnswerId(a.getExamPaperAnswerId());
                                    ivm.setStatus(a.getStatus());
                                });
                    }
                    return ivm;
                }
        ).collect(Collectors.toList());
    }


}
