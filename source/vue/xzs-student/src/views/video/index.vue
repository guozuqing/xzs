<template>
  <div class="video-learning">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">视频教学</h2>
      <p class="page-subtitle">在线学习视频课程</p>
    </div>

    <!-- 学科筛选 -->
    <div class="subject-filter" v-loading="subjectLoading">
      <el-button 
        :type="selectedSubject === null ? 'primary' : ''" 
        size="small" 
        @click="filterBySubject(null)"
        class="filter-btn"
      >全部</el-button>
      <el-button 
        v-for="subject in subjects" 
        :key="subject.id"
        :type="selectedSubject === subject.id ? 'primary' : ''" 
        size="small" 
        @click="filterBySubject(subject.id)"
        class="filter-btn"
      >{{ subject.name }}</el-button>
    </div>

    <!-- 视频列表 -->
    <div class="video-grid" v-loading="videoLoading">
      <div 
        v-for="video in videos" 
        :key="video.id" 
        class="video-card"
        @click="playVideo(video)"
      >
        <div class="video-cover">
          <img 
            :src="video.coverUrl || require('@/assets/default-video-cover.svg')" 
            :alt="video.name"
            @error="handleImageError"
          />
          <div class="play-overlay">
            <i class="el-icon-video-play"></i>
          </div>
        </div>
        <div class="video-info">
          <h3 class="video-title">{{ video.name }}</h3>
          <p class="video-subject">{{ video.subjectName }}</p>
          <p class="video-description">{{ video.description }}</p>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!videoLoading && videos.length === 0" class="empty-state">
      <i class="el-icon-video-camera-solid"></i>
      <p>暂无视频课程</p>
    </div>
  </div>
</template>

<script>
import videoApi from '@/api/video'
import subjectApi from '@/api/subject'

export default {
  name: 'VideoLearning',
  data() {
    return {
      videos: [],
      subjects: [],
      selectedSubject: null,
      videoLoading: false,
      subjectLoading: false
    }
  },
  created() {
    this.loadSubjects()
    this.loadVideos()
  },
  methods: {
    loadSubjects() {
      this.subjectLoading = true
      subjectApi.list().then(res => {
        this.subjects = res.response || []
        this.subjectLoading = false
      }).catch(() => {
        this.subjectLoading = false
      })
    },
    loadVideos() {
      this.videoLoading = true
      videoApi.list().then(res => {
        this.videos = res.response || []
        this.videoLoading = false
      }).catch(() => {
        this.videoLoading = false
      })
    },
    filterBySubject(subjectId) {
      this.selectedSubject = subjectId
      this.videoLoading = true
      
      const apiCall = subjectId === null 
        ? videoApi.list() 
        : videoApi.listBySubject(subjectId)
      
      apiCall.then(res => {
        this.videos = res.response || []
        this.videoLoading = false
      }).catch(() => {
        this.videoLoading = false
      })
    },
    playVideo(video) {
      this.$router.push({
        path: '/video/play',
        query: { id: video.id }
      })
    },
    handleImageError(e) {
      e.target.src = require('@/assets/default-video-cover.svg')
    }
  }
}
</script>

<style lang="scss" scoped>
.video-learning {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  
  .page-title {
    color: #2c5f8a;
    font-size: 28px;
    margin-bottom: 8px;
    font-weight: 600;
  }
  
  .page-subtitle {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
}

.subject-filter {
  margin-bottom: 25px;
  text-align: center;
  
  .filter-btn {
    margin: 0 8px 8px 0;
  }
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.video-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(0,0,0,0.15);
  }
}

.video-cover {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .play-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0,0,0,0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
    
    i {
      font-size: 40px;
      color: #fff;
    }
  }
  
  &:hover .play-overlay {
    opacity: 1;
  }
}

.video-info {
  padding: 15px;
  
  .video-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .video-subject {
    font-size: 12px;
    color: #e6a23c;
    margin: 0 0 8px 0;
    font-weight: 500;
  }
  
  .video-description {
    font-size: 13px;
    color: #666;
    margin: 0;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  
  i {
    font-size: 64px;
    margin-bottom: 16px;
    display: block;
  }
  
  p {
    font-size: 16px;
    margin: 0;
  }
}
</style>
