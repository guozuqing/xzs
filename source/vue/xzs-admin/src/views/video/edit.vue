<template>
  <div class="app-container">
    <el-form :model="form" ref="form" label-width="120px" v-loading="formLoading">
      <el-form-item label="视频名称：" required>
        <el-input v-model="form.name" placeholder="请输入视频名称"></el-input>
      </el-form-item>
      <el-form-item label="所属学科：">
        <el-select v-model="form.subjectId" placeholder="请选择学科" clearable>
          <el-option v-for="item in subjects" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="视频封面：">
        <div style="display:flex;align-items:flex-start;gap:15px;">
          <el-upload
            :action="uploadAction"
            :on-success="onCoverUploadSuccess"
            :show-file-list="false"
            :with-credentials="true"
            accept="image/*"
            name="file">
            <el-button size="small" type="primary">上传封面图片</el-button>
          </el-upload>
          <img v-if="form.coverUrl" :src="form.coverUrl" style="max-height:80px;border-radius:4px;border:1px solid #eee;" />
        </div>
        <el-input v-model="form.coverUrl" placeholder="封面图片地址" size="small" style="margin-top:8px;"></el-input>
      </el-form-item>
      <el-form-item label="视频地址：">
        <el-input v-model="form.videoUrl" placeholder="主视频URL（无章节时使用）"></el-input>
        <el-upload
          class="upload-btn"
          :action="uploadAction"
          :on-success="onVideoUploadSuccess"
          :show-file-list="false"
          :with-credentials="true"
          name="file">
          <el-button size="small" type="primary">上传视频</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="视频简介：">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入视频简介"></el-input>
      </el-form-item>
      <el-form-item label="排序：">
        <el-input-number v-model="form.itemOrder" :min="0" :max="999"></el-input-number>
      </el-form-item>

      <el-divider content-position="left">章节管理</el-divider>

      <el-form-item>
        <el-button type="success" size="small" @click="addChapter">添加章节</el-button>
      </el-form-item>

      <el-table :data="form.chapters" border style="width: 100%; margin-bottom: 20px;" v-if="form.chapters.length > 0">
        <el-table-column label="序号" width="80" type="index" :index="1" />
        <el-table-column label="章节标题" min-width="200">
          <template slot-scope="{row}">
            <el-input v-model="row.title" placeholder="请输入章节标题" size="small"></el-input>
          </template>
        </el-table-column>
        <el-table-column label="视频地址" min-width="300">
          <template slot-scope="{row, $index}">
            <div style="display:flex;align-items:center;">
              <el-input v-model="row.videoUrl" placeholder="章节视频URL" size="small" style="flex:1;margin-right:8px;"></el-input>
              <el-upload
                :action="uploadAction"
                :on-success="(res) => onChapterUploadSuccess(res, $index)"
                :show-file-list="false"
                :with-credentials="true"
                name="file">
                <el-button size="mini" type="primary">上传</el-button>
              </el-upload>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="时长(秒)" width="120">
          <template slot-scope="{row}">
            <el-input-number v-model="row.duration" :min="0" size="small" controls-position="right"></el-input-number>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="100">
          <template slot-scope="{row}">
            <el-input-number v-model="row.itemOrder" :min="0" size="small" controls-position="right"></el-input-number>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{$index}">
            <el-button size="mini" type="danger" @click="removeChapter($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-form-item>
        <el-button type="primary" @click="submitForm">提交</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions } from 'vuex'
import videoApi from '@/api/video'
import subjectApi from '@/api/subject'

export default {
  data () {
    return {
      form: {
        id: null,
        name: '',
        subjectId: null,
        videoUrl: '',
        coverUrl: '',
        description: '',
        gradeLevel: null,
        itemOrder: 0,
        chapters: []
      },
      formLoading: false,
      subjects: [],
      uploadAction: (process.env.VUE_APP_URL || '') + '/api/admin/video/upload'
    }
  },
  created () {
    this.loadSubjects()
    let id = this.$route.query.id
    let _this = this
    if (id && parseInt(id) !== 0) {
      _this.formLoading = true
      videoApi.select(id).then(re => {
        let data = re.response
        _this.form = {
          id: data.id,
          name: data.name,
          subjectId: data.subjectId,
          videoUrl: data.videoUrl || '',
          coverUrl: data.coverUrl || '',
          description: data.description || '',
          gradeLevel: data.gradeLevel,
          itemOrder: data.itemOrder || 0,
          chapters: (data.chapters || []).map(c => ({
            id: c.id,
            title: c.title,
            videoUrl: c.videoUrl || '',
            duration: c.duration || 0,
            itemOrder: c.itemOrder || 0
          }))
        }
        _this.formLoading = false
      })
    }
  },
  methods: {
    loadSubjects () {
      subjectApi.list().then(data => {
        this.subjects = data.response
      })
    },
    addChapter () {
      this.form.chapters.push({
        id: null,
        title: '',
        videoUrl: '',
        duration: 0,
        itemOrder: this.form.chapters.length + 1
      })
    },
    removeChapter (index) {
      this.form.chapters.splice(index, 1)
    },
    onCoverUploadSuccess (res) {
      if (res.code === 1) {
        this.form.coverUrl = res.response
        this.$message.success('封面上传成功')
      } else {
        this.$message.error('封面上传失败')
      }
    },
    onVideoUploadSuccess (res) {
      if (res.code === 1) {
        this.form.videoUrl = res.response
        this.$message.success('视频上传成功')
      } else {
        this.$message.error('上传失败')
      }
    },
    onChapterUploadSuccess (res, index) {
      if (res.code === 1) {
        this.$set(this.form.chapters[index], 'videoUrl', res.response)
        this.$message.success('章节视频上传成功')
      } else {
        this.$message.error('上传失败')
      }
    },
    submitForm () {
      let _this = this
      if (!this.form.name) {
        this.$message.error('请输入视频名称')
        return
      }
      this.formLoading = true
      videoApi.edit(this.form).then(data => {
        if (data.code === 1) {
          _this.$message.success(data.message)
          _this.delCurrentView(_this).then(() => {
            _this.$router.push('/video/list')
          })
        } else {
          _this.$message.error(data.message)
          _this.formLoading = false
        }
      }).catch(e => {
        _this.formLoading = false
      })
    },
    resetForm () {
      let lastId = this.form.id
      this.form = {
        id: lastId,
        name: '',
        subjectId: null,
        videoUrl: '',
        coverUrl: '',
        description: '',
        gradeLevel: null,
        itemOrder: 0,
        chapters: []
      }
    },
    ...mapActions('tagsView', { delCurrentView: 'delCurrentView' })
  },
  computed: {
    ...mapGetters('enumItem', [
      'enumFormat'
    ])
  }
}
</script>

<style scoped>
.upload-btn {
  margin-top: 8px;
}
</style>
