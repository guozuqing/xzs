<template>
  <div class="exam-do-page" v-loading="formLoading">
    <!-- 顶部标题栏 -->
    <div class="exam-top-bar">
      <span class="exam-top-title">{{ form.name }}</span>
    </div>

    <div class="exam-three-col">
      <!-- 左侧：考试信息 + 倒计时 -->
      <div class="exam-left-panel">
        <div class="exam-info-block">
          <div class="info-avatar">
            <i class="el-icon-user" style="font-size: 40px; color: #2c5f8a;"></i>
          </div>
          <div class="info-item"><label>试卷名称：</label><span>{{ form.name }}</span></div>
          <div class="info-item"><label>试卷总分：</label><span>{{ form.score }} 分</span></div>
          <div class="info-item"><label>题目数量：</label><span>{{ totalQuestionCount }} 题</span></div>
          <div class="info-item"><label>考试时长：</label><span>{{ form.suggestTime }} 分钟</span></div>
        </div>
        <div class="exam-timer-block">
          <div class="timer-label">剩余时间</div>
          <div class="timer-value" :class="{ 'timer-warning': remainTime < 300 }">{{ formatSeconds(remainTime) }}</div>
        </div>
        <div class="exam-submit-block">
          <el-button type="primary" class="submit-btn" @click="submitForm">交 卷</el-button>
        </div>
      </div>

      <!-- 中间：题目区域 -->
      <div class="exam-center-panel">
        <el-form :model="form" ref="form" label-width="50px">
          <div :key="index" v-for="(titleItem, index) in form.titleItems">
            <div class="question-section-title">{{ titleItem.name }}</div>
            <div
              v-for="questionItem in titleItem.questionItems"
              :key="questionItem.itemOrder"
              :id="'question-' + questionItem.itemOrder"
              class="question-card"
              :class="{ 'question-active': currentQuestion === questionItem.itemOrder }"
            >
              <div class="question-header">
                <span class="question-order">{{ form.name }} 题{{ questionItem.itemOrder }}</span>
                <el-tag size="mini" :type="getQuestionTypeTag(questionItem.questionType)">{{ getQuestionTypeName(questionItem.questionType) }}</el-tag>
                <span class="question-score">（{{ questionItem.score }} 分）</span>
              </div>
              <el-form-item :label="questionItem.itemOrder + '.'" label-width="40px" class="exam-question-item">
                <QuestionEdit :qType="questionItem.questionType" :question="questionItem"
                              :answer="answer.answerItems[questionItem.itemOrder - 1]"/>
              </el-form-item>
            </div>
          </div>
        </el-form>
      </div>

      <!-- 右侧：答题卡 -->
      <div class="exam-right-panel">
        <div class="answer-card-title">答题卡</div>
        <div class="answer-card-stats">
          <span class="stat-item stat-answered">已答：{{ answeredCount }}</span>
          <span class="stat-item stat-unanswered">未答：{{ totalQuestionCount - answeredCount }}</span>
        </div>
        <div class="answer-card-grid">
          <span
            v-for="item in answer.answerItems"
            :key="item.itemOrder"
            class="answer-card-item"
            :class="{
              'item-completed': item.completed,
              'item-current': currentQuestion === item.itemOrder
            }"
            @click="goAnchor('#question-' + item.itemOrder)"
          >{{ item.itemOrder }}</span>
        </div>
        <div class="answer-card-legend">
          <span class="legend-item"><span class="legend-dot completed-dot"></span>已答</span>
          <span class="legend-item"><span class="legend-dot uncompleted-dot"></span>未答</span>
          <span class="legend-item"><span class="legend-dot current-dot"></span>当前</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { formatSeconds } from '@/utils'
import QuestionEdit from '../components/QuestionEdit'
import examPaperApi from '@/api/examPaper'
import examPaperAnswerApi from '@/api/examPaperAnswer'

export default {
  components: { QuestionEdit },
  data () {
    return {
      form: {
        name: '',
        score: '',
        suggestTime: 0,
        titleItems: []
      },
      formLoading: false,
      answer: {
        questionId: null,
        doTime: 0,
        answerItems: []
      },
      timer: null,
      remainTime: 0,
      currentQuestion: 1,
      passScore: 0
    }
  },
  created () {
    let id = this.$route.query.id
    this.passScore = parseFloat(this.$route.query.passScore || '0')
    let _this = this
    if (id && parseInt(id) !== 0) {
      _this.formLoading = true
      examPaperApi.select(id).then(re => {
        _this.form = re.response
        _this.remainTime = re.response.suggestTime * 60
        _this.initAnswer()
        _this.timeReduce()
        _this.formLoading = false
      })
    }
  },
  beforeDestroy () {
    window.clearInterval(this.timer)
  },
  methods: {
    formatSeconds (theTime) {
      return formatSeconds(theTime)
    },
    timeReduce () {
      let _this = this
      this.timer = setInterval(function () {
        if (_this.remainTime <= 0) {
          _this.submitForm()
        } else {
          ++_this.answer.doTime
          --_this.remainTime
        }
      }, 1000)
    },
    goAnchor (selector) {
      let el = this.$el.querySelector(selector)
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' })
        let order = parseInt(selector.replace('#question-', ''))
        this.currentQuestion = order
      }
    },
    initAnswer () {
      this.answer.id = this.form.id
      let titleItemArray = this.form.titleItems
      for (let tIndex in titleItemArray) {
        let questionArray = titleItemArray[tIndex].questionItems
        for (let qIndex in questionArray) {
          let question = questionArray[qIndex]
          this.answer.answerItems.push({ questionId: question.id, content: null, contentArray: [], completed: false, itemOrder: question.itemOrder })
        }
      }
    },
    submitForm () {
      let _this = this
      this.$confirm('确定要交卷吗？', '提示', {
        confirmButtonText: '确定交卷',
        cancelButtonText: '继续答题',
        type: 'warning'
      }).then(() => {
        window.clearInterval(_this.timer)
        _this.formLoading = true
        examPaperAnswerApi.answerSubmit(_this.answer).then(re => {
          if (re.code === 1) {
            let score = parseFloat(re.response)
            let passed = score >= _this.passScore
            let resultText = passed ? '合格' : '不合格'
            let resultColor = passed ? 'color:#67c23a;font-weight:bold' : 'color:#f56c6c;font-weight:bold'
            _this.$alert(
              '<div style="text-align:center;font-size:16px;">' +
              '<p>试卷得分：<strong>' + re.response + '</strong> 分</p>' +
              '<p>合格分数：<strong>' + _this.passScore + '</strong> 分</p>' +
              '<p>考试结果：<span style="' + resultColor + ';font-size:20px;">' + resultText + '</span></p>' +
              '</div>',
              '考试结果',
              {
                dangerouslyUseHTMLString: true,
                confirmButtonText: '返回考试记录',
                callback: action => {
                  _this.$router.push('/record/index')
                }
              }
            )
          } else {
            _this.$message.error(re.message)
          }
          _this.formLoading = false
        }).catch(e => {
          _this.formLoading = false
        })
      }).catch(() => {})
    },
    getQuestionTypeName (type) {
      const map = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }
      return map[type] || '未知'
    },
    getQuestionTypeTag (type) {
      const map = { 1: '', 2: 'success', 3: 'info', 4: 'warning', 5: 'danger' }
      return map[type] || ''
    }
  },
  computed: {
    totalQuestionCount () {
      return this.answer.answerItems.length
    },
    answeredCount () {
      return this.answer.answerItems.filter(i => i.completed).length
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-do-page {
  min-height: 100vh;
  background: #f0f2f5;
}

.exam-top-bar {
  background: linear-gradient(135deg, #1a3a5c, #2c5f8a);
  color: #fff;
  text-align: center;
  padding: 12px 0;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 2px;
  position: sticky;
  top: 0;
  z-index: 999;
}

.exam-three-col {
  display: flex;
  max-width: 1400px;
  margin: 15px auto;
  padding: 0 15px;
  gap: 15px;
  align-items: flex-start;
}

.exam-left-panel {
  width: 220px;
  min-width: 220px;
  position: sticky;
  top: 60px;
}

.exam-info-block {
  background: #fff;
  border-radius: 8px;
  padding: 20px 15px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  margin-bottom: 12px;
  text-align: center;
}

.info-avatar {
  margin-bottom: 12px;
}

.info-item {
  font-size: 13px;
  color: #666;
  line-height: 2;
  text-align: left;

  label {
    color: #999;
  }

  span {
    color: #333;
  }
}

.exam-timer-block {
  background: #fff;
  border-radius: 8px;
  padding: 20px 15px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  text-align: center;
  margin-bottom: 12px;
}

.timer-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.timer-value {
  font-size: 24px;
  font-weight: 700;
  color: #2c5f8a;
  font-family: 'Courier New', monospace;
}

.timer-warning {
  color: #f56c6c;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.exam-submit-block {
  text-align: center;
}

.submit-btn {
  width: 100%;
  height: 42px;
  font-size: 16px;
  letter-spacing: 8px;
  background: #2c5f8a;
  border-color: #2c5f8a;
  border-radius: 8px;

  &:hover {
    background: #3a7cb8;
    border-color: #3a7cb8;
  }
}

.exam-center-panel {
  flex: 1;
  min-width: 0;
}

.question-section-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c5f8a;
  padding: 12px 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border-left: 4px solid #2c5f8a;
}

.question-card {
  background: #fff;
  border-radius: 8px;
  padding: 15px 20px;
  margin-bottom: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  border-left: 3px solid transparent;
  transition: border-color 0.3s;

  &.question-active {
    border-left-color: #2c5f8a;
  }
}

.question-header {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.question-order {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.question-score {
  font-size: 13px;
  color: #999;
}

.exam-question-item {
  margin-bottom: 0;
}

.exam-right-panel {
  width: 200px;
  min-width: 200px;
  background: #fff;
  border-radius: 8px;
  padding: 20px 15px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  position: sticky;
  top: 60px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
}

.answer-card-title {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #2c5f8a;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.answer-card-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 12px;
  font-size: 12px;
}

.stat-answered {
  color: #67c23a;
}

.stat-unanswered {
  color: #f56c6c;
}

.answer-card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: flex-start;
}

.answer-card-item {
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #f5f5f5;
  color: #666;
  border: 1px solid #e4e7ed;
  transition: all 0.2s;

  &:hover {
    border-color: #2c5f8a;
    color: #2c5f8a;
  }

  &.item-completed {
    background: #67c23a;
    color: #fff;
    border-color: #67c23a;
  }

  &.item-current {
    border-color: #2c5f8a;
    box-shadow: 0 0 0 2px rgba(44,95,138,0.3);
  }
}

.answer-card-legend {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #999;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 2px;
  display: inline-block;
}

.completed-dot {
  background: #67c23a;
}

.uncompleted-dot {
  background: #f5f5f5;
  border: 1px solid #e4e7ed;
}

.current-dot {
  background: #fff;
  border: 2px solid #2c5f8a;
}
</style>
