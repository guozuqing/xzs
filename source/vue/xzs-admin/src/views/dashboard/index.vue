<template>
  <div class="dashboard-container">
    <el-row :gutter="24" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel card-exam">
          <div class="card-panel-icon-wrapper">
            <svg-icon icon-class="exam" class-name="card-panel-icon"/>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">试卷总数</div>
            <count-to :start-val="0" :end-val="examPaperCount" :duration="2600" class="card-panel-num" v-loading="loading"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel card-question">
          <div class="card-panel-icon-wrapper">
            <svg-icon icon-class="question" class-name="card-panel-icon"/>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">题目总数</div>
            <count-to :start-val="0" :end-val="questionCount" :duration="3000" class="card-panel-num" v-loading="loading"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel card-answer">
          <div class="card-panel-icon-wrapper">
            <svg-icon icon-class="doexampaper" class-name="card-panel-icon"/>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">答卷总数</div>
            <count-to :start-val="0" :end-val="doExamPaperCount" :duration="3600" class="card-panel-num" v-loading="loading"/>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel card-do">
          <div class="card-panel-icon-wrapper">
            <svg-icon icon-class="doquestion" class-name="card-panel-icon"/>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">答题总数</div>
            <count-to :start-val="0" :end-val="doQuestionCount" :duration="3200" class="card-panel-num" v-loading="loading"/>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-row class="echarts-line">
      <div id="echarts-moth-user" style="width: 100%;height:400px;" v-loading="loading"/>
    </el-row>
    <el-row class="echarts-line">
      <div id="echarts-moth-question" style="width: 100%;height:400px;" v-loading="loading"/>
    </el-row>
  </div>
</template>

<script>
import resize from './components/mixins/resize'
import CountTo from 'vue-count-to'
import dashboardApi from '@/api/dashboard'
export default {
  mixins: [resize],
  components: {
    CountTo
  },
  data () {
    return {
      examPaperCount: 0,
      questionCount: 0,
      doExamPaperCount: 0,
      doQuestionCount: 0,
      echartsUserAction: null,
      echartsQuestion: null,
      loading: false
    }
  },
  mounted () {
    // eslint-disable-next-line no-undef
    this.echartsUserAction = echarts.init(document.getElementById('echarts-moth-user'), 'macarons')
    // eslint-disable-next-line no-undef
    this.echartsQuestion = echarts.init(document.getElementById('echarts-moth-question'), 'macarons')
    let _this = this
    this.loading = true
    dashboardApi.index().then(re => {
      let response = re.response
      _this.examPaperCount = response.examPaperCount
      _this.questionCount = response.questionCount
      _this.doExamPaperCount = response.doExamPaperCount
      _this.doQuestionCount = response.doQuestionCount
      _this.echartsUserAction.setOption(this.option('用户活跃度', '{b}日{c}度', response.mothDayText, response.mothDayUserActionValue))
      _this.echartsQuestion.setOption(this.option('题目月数量', '{b}日{c}题', response.mothDayText, response.mothDayDoExamQuestionValue))
      this.loading = false
    })
  },
  methods: {
    option (title, formatter, label, vaule) {
      return {
        title: {
          text: title,
          x: 'center',
          textStyle: {
            color: '#1a2840',
            fontSize: 16,
            fontWeight: 600
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: formatter,
          backgroundColor: '#fff',
          borderColor: '#e0e6ed',
          textStyle: { color: '#333' }
        },
        xAxis: {
          type: 'category',
          data: label,
          axisLine: { lineStyle: { color: '#dce3ec' } },
          axisLabel: { color: '#7a8ba8' }
        },
        grid: {
          left: 10,
          right: 10,
          bottom: 20,
          top: 40,
          containLabel: true
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#eef2f8' } },
          axisLabel: { color: '#7a8ba8' }
        },
        series: [{
          data: vaule,
          type: 'line',
          smooth: true,
          lineStyle: { color: '#4A90E2', width: 2.5 },
          itemStyle: { color: '#4A90E2' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(74,144,226,0.18)' },
                { offset: 1, color: 'rgba(74,144,226,0.02)' }
              ]
            }
          }
        }]
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #f0f4ff 0%, #f7f9fc 50%, #eef3fb 100%);
  min-height: calc(100vh - 84px);
}

.panel-group {
  margin-top: 0;

  .card-panel-col {
    margin-bottom: 24px;
  }

  .card-panel {
    display: flex;
    align-items: center;
    height: 110px;
    padding: 0 24px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(74, 144, 226, 0.08);
    border: 1px solid #eef2f8;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 6px 24px rgba(74, 144, 226, 0.15);
    }

    .card-panel-icon-wrapper {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      transition: all 0.3s;
    }

    .card-panel-icon {
      font-size: 32px;
    }

    .card-panel-description {
      margin-left: 20px;
      flex: 1;

      .card-panel-text {
        font-size: 14px;
        color: #7a8ba8;
        margin-bottom: 8px;
        font-weight: 500;
      }

      .card-panel-num {
        font-size: 24px;
        font-weight: 700;
        color: #1a2840;
      }
    }
  }

  .card-exam {
    .card-panel-icon-wrapper {
      background: rgba(74, 144, 226, 0.1);
      color: #4A90E2;
    }
    &:hover .card-panel-icon-wrapper {
      background: #4A90E2;
      color: #fff;
    }
  }

  .card-question {
    .card-panel-icon-wrapper {
      background: rgba(82, 196, 191, 0.1);
      color: #52c4bf;
    }
    &:hover .card-panel-icon-wrapper {
      background: #52c4bf;
      color: #fff;
    }
  }

  .card-answer {
    .card-panel-icon-wrapper {
      background: rgba(245, 166, 35, 0.1);
      color: #f5a623;
    }
    &:hover .card-panel-icon-wrapper {
      background: #f5a623;
      color: #fff;
    }
  }

  .card-do {
    .card-panel-icon-wrapper {
      background: rgba(126, 113, 242, 0.1);
      color: #7e71f2;
    }
    &:hover .card-panel-icon-wrapper {
      background: #7e71f2;
      color: #fff;
    }
  }
}

.echarts-line {
  background: #fff;
  padding: 24px;
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(74, 144, 226, 0.08);
  border: 1px solid #eef2f8;
}

@media (max-width: 550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    margin: 0 auto;
  }

  .card-panel {
    justify-content: center;
  }
}
</style>
