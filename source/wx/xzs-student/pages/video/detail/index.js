// pages/video/detail/index.js
const app = getApp()

function fullUrl(url) {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return app.globalData.baseAPI + url
}

Page({
  data: {
    spinShow: false,
    video: {},
    chapters: [],
    currentChapterIndex: 0,
    currentVideoUrl: ''
  },
  onLoad: function (options) {
    let id = options.id
    if (id) {
      this.setData({ spinShow: true })
      this.loadVideoDetail(id)
    }
  },
  loadVideoDetail: function (id) {
    let _this = this
    app.formPost('/api/wx/student/video/select/' + id, null).then(res => {
      _this.setData({ spinShow: false })
      if (res.code === 1) {
        let data = res.response
        let chapters = data.chapters || []
        let currentUrl = ''
        if (chapters.length > 0) {
          currentUrl = fullUrl(chapters[0].videoUrl)
        } else {
          currentUrl = fullUrl(data.videoUrl)
        }
        _this.setData({
          video: data,
          chapters: chapters,
          currentVideoUrl: currentUrl,
          currentChapterIndex: 0
        })
      }
    }).catch(e => {
      _this.setData({ spinShow: false })
      app.message(e, 'error')
    })
  },
  onChapterTap: function (e) {
    let index = e.currentTarget.dataset.index
    let chapter = this.data.chapters[index]
    if (chapter && chapter.videoUrl) {
      this.setData({
        currentChapterIndex: index,
        currentVideoUrl: fullUrl(chapter.videoUrl)
      })
    }
  },
  onVideoError: function (e) {
    console.error('video error:', e.detail)
    app.message('视频加载失败，请检查视频地址', 'error')
  }
})
