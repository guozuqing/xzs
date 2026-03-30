<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
      <el-form-item label="学科">
        <el-select v-model="queryParam.subjectId" placeholder="请选择" clearable>
          <el-option v-for="item in subjects" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
        <router-link :to="{path:'/video/edit'}" class="link-left">
          <el-button type="primary">添加视频</el-button>
        </router-link>
      </el-form-item>
    </el-form>

    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column prop="id" label="Id" width="80" />
      <el-table-column prop="name" label="视频名称" />
      <el-table-column prop="subjectName" label="所属学科" width="150" />
      <el-table-column prop="description" label="简介" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template slot-scope="{row}">
          {{ row.createTime | timeFormat }}
        </template>
      </el-table-column>
      <el-table-column width="220px" label="操作" align="center">
        <template slot-scope="{row}">
          <router-link :to="{path:'/video/edit', query:{id:row.id}}" class="link-left">
            <el-button size="mini">编辑</el-button>
          </router-link>
          <el-button size="mini" type="danger" @click="delVideo(row)" class="link-left">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import videoApi from '@/api/video'
import subjectApi from '@/api/subject'

export default {
  components: { Pagination },
  filters: {
    timeFormat (date) {
      if (!date) return ''
      let d = new Date(date)
      return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0') + ' ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0')
    }
  },
  data () {
    return {
      queryParam: {
        subjectId: null,
        pageIndex: 1,
        pageSize: 10
      },
      listLoading: true,
      tableData: [],
      total: 0,
      subjects: []
    }
  },
  created () {
    this.search()
    this.loadSubjects()
  },
  methods: {
    loadSubjects () {
      subjectApi.list().then(data => {
        this.subjects = data.response
      })
    },
    search () {
      this.listLoading = true
      videoApi.pageList(this.queryParam).then(data => {
        const re = data.response
        this.tableData = re.list
        this.total = re.total
        this.queryParam.pageIndex = re.pageNum
        this.listLoading = false
      })
    },
    submitForm () {
      this.queryParam.pageIndex = 1
      this.search()
    },
    delVideo (row) {
      let _this = this
      this.$confirm('确认删除该视频?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        videoApi.deleteVideo(row.id).then(re => {
          if (re.code === 1) {
            _this.search()
            _this.$message.success(re.message)
          } else {
            _this.$message.error(re.message)
          }
        })
      })
    }
  },
  computed: {
    ...mapGetters('enumItem', [
      'enumFormat'
    ])
  }
}
</script>
