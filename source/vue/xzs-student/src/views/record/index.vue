<template>
  <div class="app-contain record-page">
    <el-row :gutter="24">
      <el-col :span="17">
        <el-card shadow="hover" class="record-table-card">
          <div slot="header" class="record-card-header">
            <span class="record-card-title">考试记录</span>
            <span class="record-card-total">共 {{total}} 条记录</span>
          </div>
          <el-table v-loading="listLoading" :data="tableData" fit highlight-current-row style="width: 100%"
                    @row-click="itemSelect" :row-class-name="tableRowClassName" size="medium">
            <el-table-column type="index" label="#" width="50px" align="center"/>
            <el-table-column prop="paperName" label="试卷名称" show-overflow-tooltip/>
            <el-table-column prop="subjectName" label="学科" width="80" align="center"/>
            <el-table-column label="状态" prop="status" width="90" align="center">
              <template slot-scope="{row}">
                <el-tag size="small" :type="statusTagFormatter(row.status)">
                  {{ statusTextFormatter(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userScore" label="得分" width="70" align="center"/>
            <el-table-column prop="passScore" label="合格分" width="70" align="center"/>
            <el-table-column label="是否合格" width="90" align="center">
              <template slot-scope="{row}">
                <el-tag size="small" :type="row.passStatus === '合格' ? 'success' : (row.passStatus === '不合格' ? 'danger' : 'info')" v-if="row.passStatus">
                  {{ row.passStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="做题时间" width="170" align="center"/>
            <el-table-column label="操作" align="center" width="90">
              <template slot-scope="{row}">
                <router-link target="_blank" :to="{path:'/edit',query:{id:row.id}}" v-if="row.status === 1">
                  <el-button type="primary" size="mini" plain>批改</el-button>
                </router-link>
                <router-link target="_blank" :to="{path:'/read',query:{id:row.id}}" v-if="row.status === 2">
                  <el-button type="primary" size="mini" plain>查看</el-button>
                </router-link>
              </template>
            </el-table-column>
          </el-table>
          <pagination v-show="total>0" :total="total" :background="false" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                      @pagination="search" style="margin-top: 20px"/>
        </el-card>
      </el-col>
      <el-col :span="7">
        <el-card shadow="hover" class="record-answer-info record-detail-card">
          <div slot="header" class="record-card-header">
            <span class="record-card-title">成绩详情</span>
          </div>
          <div class="record-detail-list">
            <div class="record-detail-row">
              <span class="record-detail-label">系统判分</span>
              <span class="record-detail-value">{{selectItem.systemScore}}</span>
            </div>
            <div class="record-detail-row">
              <span class="record-detail-label">最终得分</span>
              <span class="record-detail-value highlight-score">{{selectItem.userScore}}</span>
            </div>
            <div class="record-detail-row">
              <span class="record-detail-label">试卷总分</span>
              <span class="record-detail-value">{{selectItem.paperScore}}</span>
            </div>
            <el-divider></el-divider>
            <div class="record-detail-row">
              <span class="record-detail-label">正确题数</span>
              <span class="record-detail-value">{{selectItem.questionCorrect}}</span>
            </div>
            <div class="record-detail-row">
              <span class="record-detail-label">总题数</span>
              <span class="record-detail-value">{{selectItem.questionCount}}</span>
            </div>
            <div class="record-detail-row">
              <span class="record-detail-label">用时</span>
              <span class="record-detail-value">{{selectItem.doTime}}</span>
            </div>
            <el-divider></el-divider>
            <div class="record-detail-row">
              <span class="record-detail-label">合格分数</span>
              <span class="record-detail-value">{{selectItem.passScore || '--'}}</span>
            </div>
            <div class="record-detail-row">
              <span class="record-detail-label">是否合格</span>
              <span class="record-detail-value" :class="{'pass-success': selectItem.passStatus === '合格', 'pass-fail': selectItem.passStatus === '不合格'}">
                {{selectItem.passStatus || '--'}}
              </span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import Pagination from '@/components/Pagination'
import examPaperAnswerApi from '@/api/examPaperAnswer'
import { scrollTo } from '@/utils/scroll-to'
export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        pageIndex: 1,
        pageSize: 10
      },
      listLoading: false,
      tableData: [],
      total: 0,
      selectItem: {
        systemScore: '0',
        userScore: '0',
        doTime: '0',
        paperScore: '0',
        questionCorrect: 0,
        questionCount: 0
      }
    }
  },
  created () {
    this.search()
    scrollTo(0, 800)
  },
  methods: {
    search () {
      this.listLoading = true
      let _this = this
      examPaperAnswerApi.pageList(this.queryParam).then(data => {
        const re = data.response
        _this.tableData = re.list
        _this.total = re.total
        _this.queryParam.pageIndex = re.pageNum
        _this.listLoading = false
      })
    },
    itemSelect (row, column, event) {
      this.selectItem = row
    },
    statusTagFormatter (status) {
      return this.enumFormat(this.statusTag, status)
    },
    statusTextFormatter (status) {
      return this.enumFormat(this.statusEnum, status)
    },
    tableRowClassName ({ row }) {
      if (this.selectItem && this.selectItem.id === row.id) {
        return 'record-row-active'
      }
      return ''
    }
  },
  computed: {
    ...mapGetters('enumItem', [
      'enumFormat'
    ]),
    ...mapState('enumItem', {
      statusEnum: state => state.exam.examPaperAnswer.statusEnum,
      statusTag: state => state.exam.examPaperAnswer.statusTag
    })
  }
}
</script>

<style lang="scss" scoped>
.record-page {
  min-height: calc(100vh - 61px);
}

.record-table-card {
  border-radius: 12px;
  border: 1px solid #eef2f8;
  box-shadow: 0 2px 12px rgba(74, 144, 226, 0.08);
}

.record-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2840;
}

.record-card-total {
  font-size: 13px;
  color: #7a8ba8;
}

.record-detail-card {
  border-radius: 12px;
  border: 1px solid #eef2f8;
  box-shadow: 0 2px 12px rgba(74, 144, 226, 0.08);
  margin-top: 0;
}

.record-detail-list {
  padding: 4px 0;
}

.record-detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.record-detail-row:hover {
  background-color: #f0f4ff;
}

.record-detail-label {
  font-size: 14px;
  color: #5a6a85;
}

.record-detail-value {
  font-size: 14px;
  font-weight: 600;
  color: #1a2840;
}

.highlight-score {
  font-size: 20px;
  color: #4A90E2;
}

.pass-success {
  color: #52c4bf;
  font-weight: bold;
}

.pass-fail {
  color: #f56c6c;
  font-weight: bold;
}
</style>
<style lang="scss">
.record-page.app-contain {
  padding: 24px !important;
  background: linear-gradient(135deg, #f0f4ff 0%, #f7f9fc 50%, #eef3fb 100%) !important;
}
.record-row-active td {
  background-color: #f0f4ff !important;
}
</style>
