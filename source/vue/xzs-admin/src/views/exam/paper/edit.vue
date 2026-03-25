<template>
  <div class="app-container">
    <el-form :model="form" ref="form" label-width="120px" v-loading="formLoading" :rules="rules">
      <el-form-item label="考试名称：" prop="name" required>
        <el-input v-model="form.name" placeholder="请输入考试名称"/>
      </el-form-item>
      <el-form-item label="学科：" prop="subjectId" required>
        <el-select v-model="form.subjectId" placeholder="请选择学科">
          <el-option v-for="item in subjects" :key="item.id" :value="item.id"
                     :label="item.name"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="题目数量：" prop="questionCount" required>
        <el-input-number v-model="form.questionCount" :min="1" :max="500" placeholder="题目数量"/>
      </el-form-item>
      <el-form-item label="每题分值：" prop="scorePerQuestion" required>
        <el-input v-model="form.scorePerQuestion" placeholder="例如: 1 或 2.5"/>
      </el-form-item>
      <el-form-item label="合格分数：" prop="passScore" required>
        <el-input v-model="form.passScore" placeholder="例如: 60 或 80"/>
      </el-form-item>
      <el-form-item label="考试时长：" prop="suggestTime" required>
        <el-input-number v-model="form.suggestTime" :min="1" placeholder="分钟"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">发布考试</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

import { mapState, mapActions } from 'vuex'
import examPaperApi from '@/api/examPaper'

export default {
  data () {
    return {
      form: {
        name: '',
        subjectId: null,
        questionCount: 100,
        scorePerQuestion: '1',
        passScore: '80',
        suggestTime: 120
      },
      formLoading: false,
      rules: {
        name: [
          { required: true, message: '请输入考试名称', trigger: 'blur' }
        ],
        subjectId: [
          { required: true, message: '请选择学科', trigger: 'change' }
        ],
        questionCount: [
          { required: true, message: '请输入题目数量', trigger: 'blur' }
        ],
        scorePerQuestion: [
          { required: true, message: '请输入每题分值', trigger: 'blur' }
        ],
        passScore: [
          { required: true, message: '请输入合格分数', trigger: 'blur' }
        ],
        suggestTime: [
          { required: true, message: '请输入考试时长', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    this.initSubject()
  },
  methods: {
    submitForm () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.formLoading = true
          examPaperApi.publish(this.form).then(re => {
            if (re.code === 1) {
              _this.$message.success('考试发布成功')
              _this.delCurrentView(_this).then(() => {
                _this.$router.push('/exam/paper/list')
              })
            } else {
              _this.$message.error(re.message)
            }
            _this.formLoading = false
          }).catch(e => {
            _this.formLoading = false
          })
        } else {
          return false
        }
      })
    },
    resetForm () {
      this.$refs['form'].resetFields()
      this.form = {
        name: '',
        subjectId: null,
        questionCount: 100,
        scorePerQuestion: '1',
        passScore: '80',
        suggestTime: 120
      }
    },
    ...mapActions('exam', { initSubject: 'initSubject' }),
    ...mapActions('tagsView', { delCurrentView: 'delCurrentView' })
  },
  computed: {
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>
