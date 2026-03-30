<template>
  <div class="app-container">
    <el-card shadow="hover">
      <div slot="header">
        <span style="font-size:16px;font-weight:600;">导入试题</span>
        <span style="font-size:13px;color:#909399;margin-left:12px;">
          使用「试题录入工具.html」生成 JSON 文件后，在此处上传导入
        </span>
      </div>

      <el-upload
        ref="upload"
        drag
        action=""
        accept=".json"
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="fileList"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将 JSON 文件拖到此处，或 <em>点击选择</em></div>
        <div class="el-upload__tip" slot="tip">仅支持 .json 格式的试题文件</div>
      </el-upload>

      <div v-if="questions.length > 0" style="margin-top:20px;">
        <el-alert
          :title="'已解析 ' + questions.length + ' 道题目'"
          type="success"
          :closable="false"
          show-icon
          style="margin-bottom:16px;"
        />

        <el-table :data="questions" border size="small" max-height="400" style="width:100%;">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="questionType" label="题型" width="80">
            <template slot-scope="{row}">
              {{ typeMap[row.questionType] || '未知' }}
            </template>
          </el-table-column>
          <el-table-column prop="subjectId" label="学科ID" width="80" />
          <el-table-column label="题干" show-overflow-tooltip>
            <template slot-scope="{row}">
              {{ stripHtml(row.title) }}
            </template>
          </el-table-column>
          <el-table-column prop="difficult" label="难度" width="60" />
          <el-table-column prop="correct" label="答案" width="120" show-overflow-tooltip />
          <el-table-column label="操作" width="70" align="center">
            <template slot-scope="{$index}">
              <el-button type="text" size="mini" style="color:#F56C6C;" @click="removeQuestion($index)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div style="margin-top:20px;">
          <el-button type="primary" :loading="importing" @click="doImport">
            确认导入 {{ questions.length }} 道题目
          </el-button>
          <el-button @click="clearAll">清空</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import questionApi from '@/api/question'

export default {
  data () {
    return {
      fileList: [],
      questions: [],
      importing: false,
      typeMap: {
        1: '单选题',
        2: '多选题',
        3: '判断题',
        4: '填空题',
        5: '简答题'
      }
    }
  },
  methods: {
    handleFileChange (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const data = JSON.parse(e.target.result)
          if (!Array.isArray(data)) {
            this.$message.error('JSON 格式错误，应为数组')
            this.fileList = []
            return
          }
          this.questions = data.filter(q => q.questionType && q.subjectId && q.title)
          if (this.questions.length === 0) {
            this.$message.warning('文件中没有有效的题目数据')
          } else {
            this.$message.success('解析成功，共 ' + this.questions.length + ' 道题目')
          }
        } catch (err) {
          this.$message.error('JSON 解析失败: ' + err.message)
          this.fileList = []
        }
      }
      reader.readAsText(file.raw)
    },
    handleFileRemove () {
      this.questions = []
    },
    removeQuestion (index) {
      this.questions.splice(index, 1)
    },
    clearAll () {
      this.questions = []
      this.fileList = []
      this.$refs.upload.clearFiles()
    },
    doImport () {
      if (this.questions.length === 0) {
        this.$message.warning('没有可导入的题目')
        return
      }
      this.importing = true
      questionApi.importQuestions(this.questions).then(re => {
        this.importing = false
        if (re.code === 1) {
          this.$message.success(re.response || re.message)
          this.clearAll()
        } else {
          this.$message.error(re.message)
        }
      }).catch(() => {
        this.importing = false
        this.$message.error('导入请求失败')
      })
    },
    stripHtml (html) {
      if (!html) return ''
      const div = document.createElement('div')
      div.innerHTML = html
      return div.textContent || div.innerText || ''
    }
  }
}
</script>
