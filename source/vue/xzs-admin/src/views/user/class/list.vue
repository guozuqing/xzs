<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
      <el-form-item label="班级名称：">
        <el-input v-model="queryParam.name" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
        <el-button type="primary" @click="addClass">添加班级</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column prop="id" label="Id" width="80"/>
      <el-table-column prop="name" label="班级名称" min-width="150"/>
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip/>
      <el-table-column prop="studentCount" label="学生人数" width="100" align="center"/>
      <el-table-column prop="createTime" label="创建时间" width="170" align="center"/>
      <el-table-column width="280px" label="操作" align="center">
        <template slot-scope="{row}">
          <el-button size="mini" type="primary" @click="manageMembers(row)">管理学生</el-button>
          <el-button size="mini" @click="editClass(row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteClass(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>

    <!-- 新增/编辑班级弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="classForm" ref="classForm" label-width="100px" :rules="classRules">
        <el-form-item label="班级名称：" prop="name">
          <el-input v-model="classForm.name" placeholder="请输入班级名称"></el-input>
        </el-form-item>
        <el-form-item label="描述：">
          <el-input v-model="classForm.description" type="textarea" rows="3" placeholder="请输入班级描述"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClassForm" :loading="submitLoading">确定</el-button>
      </span>
    </el-dialog>

    <!-- 管理学生弹窗 -->
    <el-dialog :title="'管理学生 - ' + currentClassName" :visible.sync="memberDialogVisible" width="700px" :close-on-click-modal="false">
      <div style="margin-bottom: 15px; display: flex; align-items: center; gap: 10px;">
        <el-select v-model="selectedUserId" filterable remote placeholder="搜索学生用户名" :remote-method="searchStudents" :loading="studentSearchLoading" style="width: 300px;">
          <el-option v-for="item in studentOptions" :key="item.id" :value="item.id" :label="item.userName + (item.realName ? ' (' + item.realName + ')' : '')"></el-option>
        </el-select>
        <el-button type="primary" size="small" @click="addMember" :disabled="!selectedUserId">添加学生</el-button>
      </div>
      <el-table :data="memberList" border fit highlight-current-row style="width: 100%" v-loading="memberLoading" size="small">
        <el-table-column prop="userId" label="Id" width="60"/>
        <el-table-column prop="userName" label="用户名" min-width="120"/>
        <el-table-column prop="realName" label="真实姓名" min-width="120"/>
        <el-table-column prop="phone" label="手机号" min-width="120"/>
        <el-table-column prop="createTime" label="加入时间" width="170" align="center"/>
        <el-table-column width="80px" label="操作" align="center">
          <template slot-scope="{row}">
            <el-button size="mini" type="danger" @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import classApi from '@/api/classApi'
import userApi from '@/api/user'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        name: '',
        pageIndex: 1,
        pageSize: 10
      },
      listLoading: true,
      tableData: [],
      total: 0,
      dialogVisible: false,
      dialogTitle: '添加班级',
      classForm: {
        id: null,
        name: '',
        description: ''
      },
      classRules: {
        name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
      },
      submitLoading: false,
      memberDialogVisible: false,
      currentClassId: null,
      currentClassName: '',
      memberList: [],
      memberLoading: false,
      selectedUserId: null,
      studentOptions: [],
      studentSearchLoading: false
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      classApi.page(this.queryParam).then(data => {
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
    addClass () {
      this.dialogTitle = '添加班级'
      this.classForm = { id: null, name: '', description: '' }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.classForm && this.$refs.classForm.clearValidate()
      })
    },
    editClass (row) {
      this.dialogTitle = '编辑班级'
      classApi.select(row.id).then(re => {
        if (re.code === 1) {
          this.classForm = re.response
          this.dialogVisible = true
        }
      })
    },
    submitClassForm () {
      this.$refs.classForm.validate((valid) => {
        if (valid) {
          this.submitLoading = true
          classApi.edit(this.classForm).then(re => {
            if (re.code === 1) {
              this.$message.success('操作成功')
              this.dialogVisible = false
              this.search()
            } else {
              this.$message.error(re.message)
            }
            this.submitLoading = false
          }).catch(() => {
            this.submitLoading = false
          })
        }
      })
    },
    deleteClass (row) {
      this.$confirm('确定删除班级 "' + row.name + '" 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        classApi.deleteClass(row.id).then(re => {
          if (re.code === 1) {
            this.$message.success('删除成功')
            this.search()
          } else {
            this.$message.error(re.message)
          }
        })
      }).catch(() => {})
    },
    manageMembers (row) {
      this.currentClassId = row.id
      this.currentClassName = row.name
      this.selectedUserId = null
      this.studentOptions = []
      this.memberDialogVisible = true
      this.loadMembers()
    },
    loadMembers () {
      this.memberLoading = true
      classApi.members(this.currentClassId).then(re => {
        if (re.code === 1) {
          this.memberList = re.response
        }
        this.memberLoading = false
      })
    },
    searchStudents (query) {
      if (query !== '') {
        this.studentSearchLoading = true
        userApi.getUserPageList({ userName: query, role: 1, pageIndex: 1, pageSize: 20 }).then(re => {
          this.studentOptions = re.response.list
          this.studentSearchLoading = false
        })
      } else {
        this.studentOptions = []
      }
    },
    addMember () {
      if (!this.selectedUserId) return
      classApi.addMember({ classId: this.currentClassId, userId: this.selectedUserId }).then(re => {
        if (re.code === 1) {
          this.$message.success('添加成功')
          this.selectedUserId = null
          this.loadMembers()
          this.search()
        } else {
          this.$message.error(re.message)
        }
      })
    },
    removeMember (row) {
      this.$confirm('确定从班级中移除学生 "' + (row.realName || row.userName) + '" 吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        classApi.removeMember({ classId: this.currentClassId, userId: row.userId }).then(re => {
          if (re.code === 1) {
            this.$message.success('移除成功')
            this.loadMembers()
            this.search()
          } else {
            this.$message.error(re.message)
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
