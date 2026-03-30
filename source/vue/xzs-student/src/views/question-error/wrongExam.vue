<template>
  <div class="wrong-exam-page" v-loading="formLoading">
    <!-- 顶部标题栏 -->
    <div class="exam-top-bar">
      <span class="exam-top-title">错题考试</span>
    </div>

    <div class="exam-three-col" v-if="questions.length > 0">
      <!-- 左侧：考试信息 -->
      <div class="exam-left-panel">
        <div class="exam-info-block">
          <div class="info-avatar">
            <i class="el-icon-edit-outline" style="font-size: 40px; color: #2c5f8a;"></i>
          </div>
          <div class="info-item"><label>考试类型：</label><span>错题重考</span></div>
          <div class="info-item"><label>题目数量：</label><span>{{ questions.length }} 题</span></div>
          <div class="info-item"><label>考试说明：</label><span>做对的题将从错题本移除</span></div>
        </div>
        <div class="exam-submit-block">
          <el-button type="primary" class="submit-btn" @click="submitExam">交 卷</el-button>
          <el-button plain class="back-btn" @click="goBack">返回错题本</el-button>
        </div>
      </div>

      <!-- 中间：题目区域 -->
      <div class="exam-center-panel">
        <div
          v-for="(q, index) in questions"
          :key="q.wrongAnswerId"
          :id="'question-' + q.itemOrder"
          class="question-card"
          :class="{ 'question-active': currentQuestion === q.itemOrder }"
        >
          <div class="question-header">
            <span class="question-order">第{{ q.itemOrder }}题</span>
            <el-tag size="mini" :type="getQuestionTypeTag(q.questionType)">{{ getQuestionTypeName(q.questionType) }}</el-tag>
          </div>
          <div style="line-height:1.8">
            <!-- 单选题 -->
            <div v-if="q.questionType === 1">
              <div class="q-title" v-html="q.title"/>
              <div class="q-content">
                <el-radio-group v-model="answerItems[index].content" @change="answerItems[index].completed = true">
                  <el-radio v-for="item in q.items" :key="item.prefix" :label="item.prefix">
                    <span class="question-prefix">{{item.prefix}}.</span>
                    <span v-html="item.content" class="q-item-span-content"></span>
                  </el-radio>
                </el-radio-group>
              </div>
            </div>
            <!-- 多选题 -->
            <div v-else-if="q.questionType === 2">
              <div class="q-title" v-html="q.title"/>
              <div class="q-content">
                <el-checkbox-group v-model="answerItems[index].contentArray" @change="answerItems[index].completed = true">
                  <el-checkbox v-for="item in q.items" :label="item.prefix" :key="item.prefix">
                    <span class="question-prefix">{{item.prefix}}.</span>
                    <span v-html="item.content" class="q-item-span-content"></span>
                  </el-checkbox>
                </el-checkbox-group>
              </div>
            </div>
            <!-- 判断题 -->
            <div v-else-if="q.questionType === 3">
              <div class="q-title" v-html="q.title" style="display: inline;margin-right: 10px"/>
              <span style="padding-right: 10px;">(</span>
              <el-radio-group v-model="answerItems[index].content" @change="answerItems[index].completed = true">
                <el-radio v-for="item in q.items" :key="item.prefix" :label="item.prefix">
                  <span v-html="item.content" class="q-item-span-content"></span>
                </el-radio>
              </el-radio-group>
              <span style="padding-left: 10px;">)</span>
            </div>
            <!-- 填空题 -->
            <div v-else-if="q.questionType === 4">
              <div class="q-title" v-html="q.title"/>
              <div>
                <el-form-item :label="item.prefix" :key="item.prefix" v-for="item in q.items" label-width="50px" style="margin-top:10px;margin-bottom:10px;">
                  <el-input v-model="answerItems[index].contentArray[item.prefix-1]" @change="answerItems[index].completed = true"/>
                </el-form-item>
              </div>
            </div>
            <!-- 简答题 -->
            <div v-else-if="q.questionType === 5">
              <div class="q-title" v-html="q.title"/>
              <div>
                <el-input v-model="answerItems[index].content" type="textarea" rows="5" @change="answerItems[index].completed = true"/>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：答题卡 -->
      <div class="exam-right-panel">
        <div class="answer-card-title">答题卡</div>
        <div class="answer-card-stats">
          <span class="stat-item stat-answered">已答：{{ answeredCount }}</span>
          <span class="stat-item stat-unanswered">未答：{{ questions.length - answeredCount }}</span>
        </div>
        <div class="answer-card-grid">
          <span
            v-for="item in answerItems"
            :key="item.itemOrder"
            class="answer-card-item"
            :class="{
              'item-completed': item.completed,
              'item-current': currentQuestion === item.itemOrder
            }"
            @click="goAnchor('#question-' + item.itemOrder)"
          >{{ item.itemOrder }}</span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!formLoading && questions.length === 0" class="empty-state">
      <i class="el-icon-check" style="font-size:48px;color:#67c23a;"></i>
      <p>暂无错题，恭喜你！</p>
      <el-button type="primary" @click="goBack">返回错题本</el-button>
    </div>

    <!-- 结果弹窗 -->
    <el-dialog title="错题考试结果" :visible.sync="showResult" width="450px" :close-on-click-modal="false" center>
      <div class="result-content">
        <div class="result-score-circle" :class="resultData.correct === resultData.total ? 'perfect' : ''">
          <span class="result-correct">{{ resultData.correct }}</span>
          <span class="result-divider">/</span>
          <span class="result-total">{{ resultData.total }}</span>
        </div>
        <div class="result-details">
          <div class="result-item result-right">
            <i class="el-icon-check"></i>
            <span>答对 <strong>{{ resultData.correct }}</strong> 题（已从错题本移除）</span>
          </div>
          <div class="result-item result-wrong">
            <i class="el-icon-close"></i>
            <span>答错 <strong>{{ resultData.wrong }}</strong> 题（仍在错题本中）</span>
          </div>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="goBack">返回错题本</el-button>
        <el-button type="primary" @click="retryExam" v-if="resultData.wrong > 0">继续练习</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import questionAnswerApi from '@/api/questionAnswer'

export default {
  name: 'WrongExam',
  data () {
    return {
      formLoading: false,
      questions: [],
      answerItems: [],
      currentQuestion: 1,
      showResult: false,
      resultData: {
        total: 0,
        correct: 0,
        wrong: 0
      }
    }
  },
  created () {
    this.loadWrongExam()
  },
  methods: {
    loadWrongExam () {
      this.formLoading = true
      questionAnswerApi.wrongExam().then(res => {
        if (res.code === 1) {
          this.questions = res.response
          this.answerItems = this.questions.map(q => ({
            wrongAnswerId: q.wrongAnswerId,
            questionId: q.questionId,
            questionType: q.questionType,
            content: null,
            contentArray: q.questionType === 4 ? new Array(q.items ? q.items.length : 0).fill('') : [],
            completed: false,
            itemOrder: q.itemOrder
          }))
        } else {
          this.$message.warning(res.message || '暂无错题')
          this.questions = []
        }
        this.formLoading = false
      }).catch(() => {
        this.$message.error('加载错题失败')
        this.formLoading = false
      })
    },
    submitExam () {
      if (this.answeredCount === 0) {
        this.$message.warning('请至少回答一道题目')
        return
      }
      this.$confirm(
        `已答 ${this.answeredCount} 题，未答 ${this.questions.length - this.answeredCount} 题，确定交卷吗？`,
        '确认交卷',
        {
          confirmButtonText: '确定交卷',
          cancelButtonText: '继续答题',
          type: 'warning'
        }
      ).then(() => {
        this.doSubmit()
      }).catch(() => {})
    },
    doSubmit () {
      this.formLoading = true
      const submitData = this.answerItems
        .filter(a => a.completed)
        .map(a => ({
          wrongAnswerId: a.wrongAnswerId,
          questionId: a.questionId,
          content: a.content,
          contentArray: a.contentArray
        }))
      questionAnswerApi.wrongExamSubmit(submitData).then(res => {
        if (res.code === 1) {
          this.resultData = res.response
          this.showResult = true
        } else {
          this.$message.error(res.message || '提交失败')
        }
        this.formLoading = false
      }).catch(() => {
        this.$message.error('提交失败')
        this.formLoading = false
      })
    },
    retryExam () {
      this.showResult = false
      this.loadWrongExam()
    },
    goBack () {
      this.$router.push('/question/index')
    },
    goAnchor (selector) {
      let el = this.$el.querySelector(selector)
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'center' })
        let order = parseInt(selector.replace('#question-', ''))
        this.currentQuestion = order
      }
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
    answeredCount () {
      return this.answerItems.filter(i => i.completed).length
    }
  }
}
</script>

<style lang="scss" scoped>
.wrong-exam-page {
  min-height: 100vh;
  background: #f0f2f5;
}

/* 顶部标题栏 */
.exam-top-bar {
  background: linear-gradient(135deg, #1a3a5c 0%, #2c5f8a 100%);
  color: #fff;
  text-align: center;
  padding: 13px 0;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 3px;
  position: sticky;
  top: 0;
  z-index: 999;
  box-shadow: 0 2px 8px rgba(44, 95, 138, 0.2);
}

/* 三栏布局 */
.exam-three-col {
  display: flex;
  max-width: 1400px;
  margin: 16px auto;
  padding: 0 16px;
  gap: 14px;
  align-items: flex-start;
}

/* 左侧面板 */
.exam-left-panel {
  width: 210px;
  min-width: 210px;
  position: sticky;
  top: 60px;
}

.exam-info-block {
  background: #fff;
  border-radius: 8px;
  padding: 20px 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  margin-bottom: 12px;
  text-align: center;
  border-top: 3px solid #2c5f8a;
}

.info-avatar {
  margin-bottom: 12px;
  i { color: #2c5f8a !important; }
}

.info-item {
  font-size: 13px;
  color: #666;
  line-height: 2.1;
  text-align: left;
  label {
    color: #999;
    font-size: 12px;
  }
  span { color: #333; font-weight: 500; }
}

.exam-submit-block {
  display: flex;
  flex-direction: column;
  gap: 10px;

  .el-button {
    width: 100%;
    margin: 0;
  }
}

.submit-btn {
  width: 100%;
  height: 42px;
  font-size: 15px;
  letter-spacing: 6px;
  background: #2c5f8a;
  border-color: #2c5f8a;
  border-radius: 8px;
  &:hover {
    background: #3a7cb8;
    border-color: #3a7cb8;
  }
}

.back-btn {
  width: 100%;
  border-radius: 8px;
}

/* 中间题目区 */
.exam-center-panel {
  flex: 1;
  min-width: 0;
}

.question-card {
  background: #fff;
  border-radius: 8px;
  padding: 18px 22px;
  margin-bottom: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  border-left: 3px solid transparent;
  transition: border-color 0.25s, box-shadow 0.25s;

  &.question-active {
    border-left-color: #2c5f8a;
    box-shadow: 0 4px 16px rgba(44, 95, 138, 0.12);
  }
}

.question-header {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f5f5f5;
}

.question-order {
  font-weight: 600;
  color: #2c5f8a;
  font-size: 14px;
}

/* 右侧答题卡 */
.exam-right-panel {
  width: 196px;
  min-width: 196px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 60px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
}

.answer-card-title {
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  padding: 12px 0;
  background: linear-gradient(135deg, #1a3a5c 0%, #2c5f8a 100%);
  letter-spacing: 2px;
}

.answer-card-stats {
  display: flex;
  justify-content: space-around;
  padding: 10px 8px 8px;
  font-size: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.stat-answered { color: #67c23a; font-weight: 500; }
.stat-unanswered { color: #f56c6c; font-weight: 500; }

.answer-card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: flex-start;
  padding: 12px 12px 16px;
}

.answer-card-item {
  width: 32px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  background: #f5f7fa;
  color: #666;
  border: 1px solid #e4e7ed;
  transition: all 0.2s;

  &:hover {
    border-color: #2c5f8a;
    color: #2c5f8a;
    background: #ecf5ff;
  }

  &.item-completed {
    background: #67c23a;
    color: #fff;
    border-color: #67c23a;
  }

  &.item-current {
    border-color: #2c5f8a;
    box-shadow: 0 0 0 2px rgba(44, 95, 138, 0.2);
    background: #ecf5ff;
    color: #2c5f8a;
  }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  p {
    font-size: 16px;
    color: #666;
    margin: 20px 0;
  }
}

/* 结果弹窗 */
.result-content {
  text-align: center;
  padding: 10px 0;
}

.result-score-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ecf5ff, #d9ecff);
  border: 4px solid #2c5f8a;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;

  &.perfect {
    background: linear-gradient(135deg, #f0f9eb, #e1f3d8);
    border-color: #67c23a;
  }
}

.result-correct {
  font-size: 36px;
  font-weight: 700;
  color: #2c5f8a;
}

.result-divider {
  font-size: 24px;
  color: #ccc;
  margin: 0 4px;
}

.result-total {
  font-size: 24px;
  color: #999;
}

.result-details { margin-top: 10px; }

.result-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 14px;

  &.result-right {
    color: #67c23a;
    i { font-size: 18px; }
  }

  &.result-wrong {
    color: #f56c6c;
    i { font-size: 18px; }
  }
}
</style>
