<template>
  <div class="app-contain error-page">
    <el-row :gutter="24">
      <el-col :span="13">
        <el-card shadow="hover" class="error-table-card">
          <div slot="header" class="error-card-header">
            <span class="error-card-title">错题本</span>
            <div class="error-card-actions">
              <span class="error-card-total">共 {{total}} 道错题</span>
              <el-button type="primary" size="small" icon="el-icon-edit-outline" @click="startWrongExam" :disabled="total === 0">错题考试</el-button>
            </div>
          </div>
          <el-table v-loading="listLoading" :data="tableData" fit highlight-current-row style="width: 100%"
                    @row-click="itemSelect" :row-class-name="tableRowClassName" size="medium">
            <el-table-column type="index" label="#" width="50" align="center"/>
            <el-table-column prop="shortTitle" label="题干" show-overflow-tooltip min-width="180"/>
            <el-table-column prop="questionType" label="题型" :formatter="questionTypeFormatter" width="80" align="center"/>
            <el-table-column prop="subjectName" label="学科" width="80" align="center"/>
            <el-table-column prop="createTime" label="做题时间" width="170" align="center"/>
          </el-table>
          <pagination v-show="total>0" :total="total" :background="false" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                      @pagination="search" style="margin-top: 20px"/>
        </el-card>
      </el-col>
      <el-col :span="11">
        <el-card shadow="hover" class="record-answer-info error-detail-card">
          <div slot="header" class="error-card-header">
            <span class="error-card-title">题目详情</span>
          </div>
          <div class="error-answer-content">
            <QuestionAnswerShow :qType="selectItem.questionType" :qLoading="qAnswerLoading" :question="selectItem.questionItem" :answer="selectItem.answerItem"/>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import Pagination from '@/components/Pagination'
import questionAnswerApi from '@/api/questionAnswer'
import QuestionAnswerShow from '../exam/components/QuestionAnswerShow'

export default {
  components: { Pagination, QuestionAnswerShow },
  data () {
    return {
      queryParam: {
        pageIndex: 1,
        pageSize: 10
      },
      listLoading: false,
      tableData: [],
      total: 0,
      qAnswerLoading: false,
      selectItem: {
        questionType: 0,
        questionItem: null,
        answerItem: null
      }
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      let _this = this
      questionAnswerApi.pageList(this.queryParam).then(data => {
        const re = data.response
        _this.tableData = re.list
        _this.total = re.total
        _this.queryParam.pageIndex = re.pageNum
        _this.listLoading = false
        if (re.list.length !== 0) {
          _this.qAnswerShow(re.list[0].id)
        }
      })
    },
    itemSelect (row, column, event) {
      this.qAnswerShow(row.id)
    },
    qAnswerShow (id) {
      let _this = this
      this.qAnswerLoading = true
      questionAnswerApi.select(id).then(re => {
        let response = re.response
        _this.selectItem.questionType = response.questionVM.questionType
        _this.selectItem.questionItem = response.questionVM
        _this.selectItem.answerItem = response.questionAnswerVM
        _this.qAnswerLoading = false
      })
    },
    questionTypeFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.questionTypeEnum, cellValue)
    },
    startWrongExam () {
      this.$router.push('/question/wrongExam')
    },
    tableRowClassName ({ row }) {
      if (this.selectItem && this.selectItem.id === row.id) {
        return 'error-row-active'
      }
      return ''
    }
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      questionTypeEnum: state => state.exam.question.typeEnum
    })
  }
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: calc(100vh - 61px);
}

.error-table-card, .error-detail-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.error-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.error-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #2c5f8a;
}

.error-card-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.error-card-total {
  font-size: 13px;
  color: #999;
}

.error-answer-content {
  padding: 8px 0;
  min-height: 200px;
}
</style>
<style lang="scss">
.error-page.app-contain {
  padding: 24px !important;
  background: #f0f2f5 !important;
}
.error-row-active td {
  background-color: #ecf5ff !important;
}
</style>
