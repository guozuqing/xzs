<template>
  <div class="app-container">
    <el-form :inline="true">
      <el-form-item>
        <router-link :to="{path:'/exam/paper/edit'}">
          <el-button type="primary">发布考试</el-button>
        </router-link>
      </el-form-item>
    </el-form>
    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column prop="id" label="Id" width="70px"/>
      <el-table-column prop="subjectId" label="学科" :formatter="subjectFormatter" width="120px" />
      <el-table-column prop="name" label="考试名称"  />
      <el-table-column prop="questionCount" label="题目数量" width="100px"/>
      <el-table-column prop="score" label="总分" :formatter="scoreFormatter" width="80px"/>
      <el-table-column prop="passScore" label="合格分数" :formatter="scoreFormatter" width="100px"/>
      <el-table-column prop="suggestTime" label="时长(分钟)" width="100px"/>
      <el-table-column prop="createTime" label="创建时间" width="160px"/>
      <el-table-column  label="操作" align="center"  width="100px">
        <template slot-scope="{row}">
          <el-button size="mini" type="danger"  @click="deleteConfig(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions } from 'vuex'
import examPaperApi from '@/api/examPaper'

export default {
  data () {
    return {
      listLoading: true,
      tableData: []
    }
  },
  created () {
    this.initSubject()
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      examPaperApi.list().then(data => {
        this.tableData = data.response
        this.listLoading = false
      })
    },
    deleteConfig (row) {
      let _this = this
      examPaperApi.deletePaper(row.id).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    scoreFormatter (row, column, cellValue, index) {
      if (cellValue == null) return ''
      return cellValue % 10 === 0 ? (cellValue / 10) + '' : (cellValue / 10.0).toFixed(1)
    },
    subjectFormatter (row, column, cellValue, index) {
      return this.subjectEnumFormat(cellValue)
    },
    ...mapActions('exam', { initSubject: 'initSubject' })
  },
  computed: {
    ...mapGetters('exam', ['subjectEnumFormat']),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>
