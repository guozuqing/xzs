<template>
  <div class="video-play">
    <!-- 深色播放主区 -->
    <div class="play-stage">
      <div class="stage-inner">
        <!-- 面包屑 -->
        <div class="stage-nav">
          <span class="nav-back" @click="goBack">
            <i class="el-icon-arrow-left"></i> 视频教学
          </span>
          <span class="nav-sep">/</span>
          <span class="nav-cur">{{ video ? video.name : '加载中...' }}</span>
        </div>

        <div class="stage-body" v-loading="loading">
          <!-- 左：播放器 -->
          <div class="player-col" v-if="video">
            <div class="player-box">
              <video
                ref="videoPlayer"
                :src="currentVideoUrl"
                controls
                class="video-player"
                @loadedmetadata="onVideoLoaded"
                @error="onVideoError"
              >
                您的浏览器不支持视频播放
              </video>
            </div>

            <!-- 视频标题和信息 -->
            <div class="video-meta-bar">
              <div class="meta-title">{{ video.name }}</div>
              <div class="meta-tags">
                <span class="meta-tag subject">{{ video.subjectName }}</span>
                <span class="meta-tag level" v-if="video.gradeLevel">难度：{{ video.gradeLevel }}</span>
                <span class="meta-tag chapter-cnt" v-if="video.chapters && video.chapters.length">
                  {{ video.chapters.length }} 章节
                </span>
              </div>
              <div class="meta-desc" v-if="video.description">{{ video.description }}</div>
            </div>
          </div>

          <!-- 右：章节列表 -->
          <div class="chapters-col" v-if="video && video.chapters && video.chapters.length > 0">
            <div class="chapters-header">
              <span class="chapters-title">课程章节</span>
              <span class="chapters-total">{{ video.chapters.length }} 节</span>
            </div>
            <div class="chapters-scroll">
              <div
                v-for="(chapter, index) in video.chapters"
                :key="chapter.id"
                class="chapter-row"
                :class="{ active: currentChapter && currentChapter.id === chapter.id }"
                @click="playChapter(chapter)"
              >
                <div class="chapter-idx">{{ index + 1 }}</div>
                <div class="chapter-body">
                  <div class="chapter-name">{{ chapter.title }}</div>
                  <div class="chapter-dur" v-if="chapter.duration">
                    <i class="el-icon-time"></i> {{ formatDuration(chapter.duration) }}
                  </div>
                </div>
                <i
                  class="el-icon-video-play chapter-play-ic"
                  v-if="currentChapter && currentChapter.id === chapter.id"
                ></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <div class="action-bar-inner">
        <el-button size="small" icon="el-icon-arrow-left" @click="goBack">返回视频列表</el-button>
        <span class="playing-hint" v-if="currentChapter">
          <i class="el-icon-video-play"></i> 正在播放：{{ currentChapter.title }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import videoApi from '@/api/video'

export default {
  name: 'VideoPlay',
  data() {
    return {
      video: null,
      currentChapter: null,
      currentVideoUrl: '',
      loading: false
    }
  },
  created() {
    this.loadVideo()
  },
  methods: {
    loadVideo() {
      const videoId = this.$route.query.id
      if (!videoId) {
        this.$message.error('视频ID不能为空')
        this.goBack()
        return
      }
      this.loading = true
      videoApi.select(videoId).then(res => {
        this.video = res.response
        if (this.video.chapters && this.video.chapters.length > 0) {
          this.playChapter(this.video.chapters[0])
        } else if (this.video.videoUrl) {
          this.currentVideoUrl = this.video.videoUrl
        }
        this.loading = false
      }).catch(err => {
        this.$message.error('加载视频失败: ' + (err || '未知错误'))
        this.loading = false
      })
    },
    playChapter(chapter) {
      this.currentChapter = chapter
      this.currentVideoUrl = chapter.videoUrl
      this.$nextTick(() => {
        if (this.$refs.videoPlayer) {
          this.$refs.videoPlayer.load()
        }
      })
    },
    onVideoLoaded() {},
    onVideoError() {
      this.$message.error('视频加载失败，请检查网络连接或联系管理员')
    },
    formatDuration(seconds) {
      if (!seconds) return ''
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins}:${secs.toString().padStart(2, '0')}`
    },
    goBack() {
      this.$router.push('/video/index')
    }
  }
}
</script>

<style lang="scss" scoped>
/* ===== 整体容器 ===== */
.video-play {
  min-height: calc(100vh - 61px);
  background: #f0f2f5;
  display: flex;
  flex-direction: column;
}

/* ===== 播放主区 ===== */
.play-stage {
  flex: 1;
  padding: 0 0 24px;
}

.stage-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 面包屑 */
.stage-nav {
  padding: 14px 0 12px;
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 6px;

  .nav-back {
    cursor: pointer;
    color: #2c5f8a;
    transition: color 0.2s;
    &:hover { color: #3a7cb8; }
    i { margin-right: 2px; }
  }

  .nav-sep { color: #ccc; }

  .nav-cur {
    color: #333;
    max-width: 400px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

/* 主体左右布局 */
.stage-body {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

/* ===== 左侧播放区 ===== */
.player-col {
  flex: 1;
  min-width: 0;
}

.player-box {
  width: 100%;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);

  .video-player {
    width: 100%;
    height: auto;
    max-height: 540px;
    display: block;
  }
}

/* 视频信息卡 */
.video-meta-bar {
  margin-top: 14px;
  padding: 18px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  border-left: 4px solid #2c5f8a;

  .meta-title {
    font-size: 18px;
    font-weight: 600;
    color: #1a1a1a;
    line-height: 1.4;
    margin-bottom: 10px;
  }

  .meta-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 10px;
  }

  .meta-tag {
    font-size: 12px;
    padding: 2px 10px;
    border-radius: 12px;
    font-weight: 500;

    &.subject {
      background: #fef6e9;
      color: #e6a23c;
      border: 1px solid #f5dba0;
    }

    &.level {
      background: #ecf5ff;
      color: #2c5f8a;
      border: 1px solid #b3d4f0;
    }

    &.chapter-cnt {
      background: #f0f9eb;
      color: #67c23a;
      border: 1px solid #c2e7b0;
    }
  }

  .meta-desc {
    font-size: 13px;
    color: #888;
    line-height: 1.7;
    padding-top: 10px;
    border-top: 1px solid #f0f0f0;
  }
}

/* ===== 右侧章节列表 ===== */
.chapters-col {
  width: 290px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  position: sticky;
  top: 16px;
  max-height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.chapters-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: linear-gradient(135deg, #1a3a5c 0%, #2c5f8a 100%);
  border-bottom: 1px solid #e4e7ed;

  .chapters-title {
    font-size: 14px;
    font-weight: 600;
    color: #fff;
    letter-spacing: 1px;
  }

  .chapters-total {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.7);
    background: rgba(255, 255, 255, 0.15);
    padding: 2px 8px;
    border-radius: 10px;
  }
}

.chapters-scroll {
  flex: 1;
  overflow-y: auto;

  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-track { background: #f5f5f5; }
  &::-webkit-scrollbar-thumb {
    background: #d0d0d0;
    border-radius: 2px;
  }
}

.chapter-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.2s;

  &:hover {
    background: #f8fbff;
  }

  &.active {
    background: #e8f2fb;
    border-left: 3px solid #2c5f8a;
    padding-left: 11px;

    .chapter-idx {
      background: #2c5f8a;
      color: #fff;
    }

    .chapter-name {
      color: #2c5f8a;
      font-weight: 600;
    }
  }

  .chapter-idx {
    width: 26px;
    height: 26px;
    border-radius: 50%;
    background: #f0f0f0;
    color: #888;
    font-size: 12px;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    transition: all 0.2s;
  }

  .chapter-body {
    flex: 1;
    min-width: 0;

    .chapter-name {
      font-size: 13px;
      color: #333;
      line-height: 1.4;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      transition: color 0.2s;
    }

    .chapter-dur {
      font-size: 11px;
      color: #bbb;
      margin-top: 3px;
      i { margin-right: 2px; }
    }
  }

  .chapter-play-ic {
    color: #2c5f8a;
    font-size: 16px;
    flex-shrink: 0;
    animation: blink 1.6s ease-in-out infinite;
  }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

/* ===== 底部操作栏 ===== */
.action-bar {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 10px 0;
}

.action-bar-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 20px;

  .playing-hint {
    font-size: 13px;
    color: #888;
    i {
      color: #2c5f8a;
      margin-right: 4px;
    }
  }
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .stage-body {
    flex-direction: column;
  }

  .chapters-col {
    width: 100%;
    position: static;
    max-height: 320px;
  }
}
</style>
