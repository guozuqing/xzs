<template>
  <div class="exam-dashboard">
    <!-- 背景横幅 -->
    <div class="exam-banner">
      <div class="banner-overlay">
        <h1 class="banner-title">民用无人驾驶航空器操控员模拟考试系统</h1>
      </div>
    </div>

    <div class="exam-main-container" v-loading="configLoading">
      <el-row :gutter="30">
        <!-- 左侧：考试须知 -->
        <el-col :span="9">
          <div class="notice-card">
            <h3 class="notice-title">考试须知:</h3>
            <ol class="notice-list">
              <li>对于民用无人驾驶航空器在非真实飞行环境进行飞行的，需要通过在线考试</li>
              <li>倒计时结束后将自动提交交卷</li>
              <li>考题类型包括单选题和多选题</li>
              <li>多选题错选或漏选均不得分</li>
              <li>考试过程中禁止切换浏览器页签，切换页面超过两次，视操作违规处理</li>
              <li>答题结束后，点击提交按钮可以自行完成考试</li>
              <li>考试结束后可查看考试结果</li>
            </ol>
            <div class="notice-footer">
              <el-checkbox v-model="agreeNotice">我已阅读《考试须知》，并同意遵守考试制度</el-checkbox>
            </div>
          </div>
        </el-col>

        <!-- 右侧：用户信息和考试列表 -->
        <el-col :span="15">
          <div class="exam-info-card">
            <h2 class="system-title">民用无人驾驶航空器操控员模拟考试系统</h2>
            <p class="user-info-text">
              姓名：<strong>{{ examConfig.realName || '--' }}</strong>
              &nbsp;&nbsp;&nbsp;&nbsp;
              身份证号：<strong>{{ examConfig.userName || '--' }}</strong>
            </p>

            <!-- 考试项目列表 -->
            <div class="exam-items-list">
              <div
                v-for="(item, index) in examConfig.examItems"
                :key="index"
                class="exam-item-row"
                :class="{ 'exam-item-selected': selectedExamIndex === index }"
                @click="selectedExamIndex = index"
              >
                <div class="exam-item-left">
                  <el-radio :label="index" v-model="selectedExamIndex" class="exam-radio">
                    <span class="exam-item-name">{{ item.examName }}</span>
                  </el-radio>
                  <el-tag size="mini" type="warning" class="exam-tag">无人机{{ examConfig.userTypeName }}执照</el-tag>
                  <span class="exam-item-date" v-if="item.lastTime">{{ item.lastTime }}</span>
                </div>
                <div class="exam-item-right" v-if="item.lastScore !== null && item.lastScore !== undefined">
                  <span class="last-exam-info">
                    上次&nbsp;&nbsp;考试成绩：<strong>{{ item.lastScore }}分</strong>&nbsp;&nbsp;
                    考试结果：<strong :class="item.lastResult === '通过' ? 'result-pass' : 'result-fail'">{{ item.lastResult }}</strong>&nbsp;&nbsp;
                    <span v-if="item.lastTime">{{ item.lastTime }}</span>
                  </span>
                </div>
              </div>
            </div>

            <!-- 开始考试按钮 -->
            <div class="start-exam-btn-wrapper">
              <el-button
                type="primary"
                size="large"
                class="start-exam-btn"
                :loading="generateLoading"
                :disabled="!agreeNotice"
                @click="startExam"
              >开始考试</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import indexApi from '@/api/dashboard'
export default {
  data () {
    return {
      examConfig: {
        realName: '',
        userName: '',
        userTypeName: '',
        userLevel: null,
        examItems: []
      },
      selectedExamIndex: 0,
      configLoading: false,
      generateLoading: false,
      agreeNotice: false
    }
  },
  created () {
    this.loadExamConfig()
  },
  methods: {
    loadExamConfig () {
      let _this = this
      _this.configLoading = true
      indexApi.examConfig().then(re => {
        _this.examConfig = re.response
        _this.configLoading = false
      }).catch(() => {
        _this.configLoading = false
      })
    },
    startExam () {
      let _this = this
      let selectedItem = this.examConfig.examItems[this.selectedExamIndex]
      if (!selectedItem) {
        this.$message.warning('请选择考试项目')
        return
      }
      if (!this.agreeNotice) {
        this.$message.warning('请先阅读并同意考试须知')
        return
      }
      _this.generateLoading = true
      indexApi.generatePaper(selectedItem).then(re => {
        _this.generateLoading = false
        if (re.code === 1) {
          let paperId = re.response.id
          let routeData = _this.$router.resolve({ path: '/do', query: { id: paperId, passScore: selectedItem.passScore } })
          window.open(routeData.href, '_blank')
        } else {
          _this.$message.error(re.message)
        }
      }).catch(() => {
        _this.generateLoading = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-dashboard {
  min-height: 100vh;
  background: #f0f2f5;
}

.exam-banner {
  width: 100%;
  height: 200px;
  background: linear-gradient(135deg, #1a3a5c 0%, #2c5f8a 50%, #3a7cb8 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="rgba(255,255,255,0.05)" d="M0,160L48,144C96,128,192,96,288,112C384,128,480,192,576,208C672,224,768,192,864,160C960,128,1056,96,1152,112C1248,128,1344,192,1392,224L1440,256L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat bottom;
    background-size: cover;
  }
}

.banner-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-title {
  color: #fff;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 4px;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.exam-main-container {
  max-width: 1200px;
  margin: -60px auto 40px auto;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

.notice-card {
  background: #fff;
  border-radius: 8px;
  padding: 30px 25px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  border-left: 4px solid #2c5f8a;
  min-height: 420px;
}

.notice-title {
  color: #2c5f8a;
  font-size: 18px;
  margin-bottom: 15px;
  font-weight: 600;
}

.notice-list {
  padding-left: 20px;
  color: #e6a23c;
  font-size: 14px;
  line-height: 2.2;

  li {
    color: #e6a23c;
  }
}

.notice-footer {
  margin-top: 20px;
  color: #999;
  font-size: 13px;
}

.exam-info-card {
  background: #fff;
  border-radius: 8px;
  padding: 30px 35px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  min-height: 420px;
}

.system-title {
  color: #2c5f8a;
  font-size: 22px;
  text-align: center;
  margin-bottom: 10px;
  font-weight: 600;
}

.user-info-text {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-bottom: 25px;
}

.exam-items-list {
  margin-bottom: 30px;
}

.exam-item-row {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 15px 20px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #2c5f8a;
    background: #f8fbff;
  }

  &.exam-item-selected {
    border-color: #2c5f8a;
    background: #f0f7ff;
  }
}

.exam-item-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.exam-radio {
  margin-right: 0;
}

.exam-item-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.exam-tag {
  margin-left: 5px;
}

.exam-item-date {
  color: #999;
  font-size: 12px;
}

.exam-item-right {
  margin-top: 8px;
  padding-left: 25px;
}

.last-exam-info {
  font-size: 13px;
  color: #e6a23c;
}

.result-pass {
  color: #67c23a;
}

.result-fail {
  color: #f56c6c;
}

.start-exam-btn-wrapper {
  text-align: center;
  margin-top: 20px;
}

.start-exam-btn {
  width: 80%;
  height: 45px;
  font-size: 16px;
  letter-spacing: 6px;
  background: #2c5f8a;
  border-color: #2c5f8a;

  &:hover {
    background: #3a7cb8;
    border-color: #3a7cb8;
  }
}
</style>
