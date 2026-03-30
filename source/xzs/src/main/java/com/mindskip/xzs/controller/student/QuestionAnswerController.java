package com.mindskip.xzs.controller.student;

import com.mindskip.xzs.base.BaseApiController;
import com.mindskip.xzs.base.RestResponse;
import com.mindskip.xzs.domain.ExamPaperQuestionCustomerAnswer;
import com.mindskip.xzs.domain.Question;
import com.mindskip.xzs.domain.Subject;
import com.mindskip.xzs.domain.TextContent;
import com.mindskip.xzs.domain.enums.QuestionTypeEnum;
import com.mindskip.xzs.domain.question.QuestionObject;
import com.mindskip.xzs.service.ExamPaperQuestionCustomerAnswerService;
import com.mindskip.xzs.service.QuestionService;
import com.mindskip.xzs.service.SubjectService;
import com.mindskip.xzs.service.TextContentService;
import com.mindskip.xzs.utility.*;
import com.mindskip.xzs.viewmodel.admin.question.QuestionEditRequestVM;
import com.mindskip.xzs.viewmodel.student.exam.ExamPaperSubmitItemVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionAnswerVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentRequestVM;
import com.mindskip.xzs.viewmodel.student.question.answer.QuestionPageStudentResponseVM;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController("StudentQuestionAnswerController")
@RequestMapping(value = "/api/student/question/answer")
public class QuestionAnswerController extends BaseApiController {

    private final ExamPaperQuestionCustomerAnswerService examPaperQuestionCustomerAnswerService;
    private final QuestionService questionService;
    private final TextContentService textContentService;
    private final SubjectService subjectService;

    @Autowired
    public QuestionAnswerController(ExamPaperQuestionCustomerAnswerService examPaperQuestionCustomerAnswerService, QuestionService questionService, TextContentService textContentService, SubjectService subjectService) {
        this.examPaperQuestionCustomerAnswerService = examPaperQuestionCustomerAnswerService;
        this.questionService = questionService;
        this.textContentService = textContentService;
        this.subjectService = subjectService;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<QuestionPageStudentResponseVM>> pageList(@RequestBody QuestionPageStudentRequestVM model) {
        model.setCreateUser(getCurrentUser().getId());
        PageInfo<ExamPaperQuestionCustomerAnswer> pageInfo = examPaperQuestionCustomerAnswerService.studentPage(model);
        PageInfo<QuestionPageStudentResponseVM> page = PageInfoHelper.copyMap(pageInfo, q -> {
            Subject subject = subjectService.selectById(q.getSubjectId());
            QuestionPageStudentResponseVM vm = modelMapper.map(q, QuestionPageStudentResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(q.getCreateTime()));
            TextContent textContent = textContentService.selectById(q.getQuestionTextContentId());
            QuestionObject questionObject = JsonUtil.toJsonObject(textContent.getContent(), QuestionObject.class);
            String clearHtml = HtmlUtil.clear(questionObject.getTitleContent());
            vm.setShortTitle(clearHtml);
            vm.setSubjectName(subject.getName());
            return vm;
        });
        return RestResponse.ok(page);
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<QuestionAnswerVM> select(@PathVariable Integer id) {
        QuestionAnswerVM vm = new QuestionAnswerVM();
        ExamPaperQuestionCustomerAnswer examPaperQuestionCustomerAnswer = examPaperQuestionCustomerAnswerService.selectById(id);
        ExamPaperSubmitItemVM questionAnswerVM = examPaperQuestionCustomerAnswerService.examPaperQuestionCustomerAnswerToVM(examPaperQuestionCustomerAnswer);
        QuestionEditRequestVM questionVM = questionService.getQuestionEditRequestVM(examPaperQuestionCustomerAnswer.getQuestionId());
        vm.setQuestionVM(questionVM);
        vm.setQuestionAnswerVM(questionAnswerVM);
        return RestResponse.ok(vm);
    }

    @RequestMapping(value = "/wrongExam", method = RequestMethod.POST)
    public RestResponse wrongExam() {
        Integer userId = getCurrentUser().getId();
        List<ExamPaperQuestionCustomerAnswer> wrongList = examPaperQuestionCustomerAnswerService.selectAllWrongByUser(userId);
        if (wrongList == null || wrongList.isEmpty()) {
            return RestResponse.fail(2, "暂无错题");
        }
        List<Map<String, Object>> questions = new ArrayList<>();
        int order = 1;
        for (ExamPaperQuestionCustomerAnswer qa : wrongList) {
            QuestionEditRequestVM questionVM = questionService.getQuestionEditRequestVM(qa.getQuestionId());
            if (questionVM == null) continue;
            Map<String, Object> item = new HashMap<>();
            item.put("wrongAnswerId", qa.getId());
            item.put("questionId", qa.getQuestionId());
            item.put("questionType", questionVM.getQuestionType());
            item.put("title", questionVM.getTitle());
            item.put("items", questionVM.getItems());
            item.put("score", questionVM.getScore());
            item.put("correct", questionVM.getCorrect());
            item.put("correctArray", questionVM.getCorrectArray());
            item.put("analyze", questionVM.getAnalyze());
            item.put("questionScore", qa.getQuestionScore());
            item.put("itemOrder", order++);
            questions.add(item);
        }
        return RestResponse.ok(questions);
    }

    @RequestMapping(value = "/wrongExam/submit", method = RequestMethod.POST)
    public RestResponse wrongExamSubmit(@RequestBody List<Map<String, Object>> answerItems) {
        int correctCount = 0;
        int totalCount = answerItems.size();
        for (Map<String, Object> answerItem : answerItems) {
            Integer wrongAnswerId = (Integer) answerItem.get("wrongAnswerId");
            Integer questionId = (Integer) answerItem.get("questionId");
            String content = (String) answerItem.get("content");
            List<String> contentArray = (List<String>) answerItem.get("contentArray");

            QuestionEditRequestVM questionVM = questionService.getQuestionEditRequestVM(questionId);
            if (questionVM == null) continue;

            boolean isRight = false;
            QuestionTypeEnum questionType = QuestionTypeEnum.fromCode(questionVM.getQuestionType());
            switch (questionType) {
                case SingleChoice:
                case TrueFalse:
                    isRight = questionVM.getCorrect() != null && questionVM.getCorrect().equals(content);
                    break;
                case MultipleChoice:
                    if (contentArray != null) {
                        String customerAnswer = ExamUtil.contentToString(contentArray);
                        isRight = customerAnswer.equals(questionVM.getCorrect());
                    }
                    break;
                case GapFilling:
                    if (contentArray != null && questionVM.getCorrectArray() != null) {
                        isRight = contentArray.equals(questionVM.getCorrectArray());
                    }
                    break;
                default:
                    break;
            }

            if (isRight) {
                ExamPaperQuestionCustomerAnswer qa = examPaperQuestionCustomerAnswerService.selectById(wrongAnswerId);
                if (qa != null) {
                    examPaperQuestionCustomerAnswerService.updateDoRightById(wrongAnswerId, true, qa.getQuestionScore());
                }
                correctCount++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", totalCount);
        result.put("correct", correctCount);
        result.put("wrong", totalCount - correctCount);
        return RestResponse.ok(result);
    }

}
