//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    spinShow: false,
    fixedPaper: [],
    pushPaper: [],
    timeLimitPaper: [],
    taskList: []
  },
  onLoad: function() {
    this.setData({
      spinShow: true
    });
    this.indexLoad()
  },
  onPullDownRefresh() {
    this.setData({
      spinShow: true
    });
    if (!this.loading) {
      this.indexLoad()
    }
  },
  goExam() {
    wx.switchTab({ url: '/pages/exam/index/index' })
  },
  goRecord() {
    wx.switchTab({ url: '/pages/record/index' })
  },
  goVideo() {
    wx.switchTab({ url: '/pages/video/index/index' })
  },
  goMy() {
    wx.switchTab({ url: '/pages/my/index/index' })
  },
  goPaperDo(e) {
    let id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/exam/do/index?id=' + id })
  },
  indexLoad: function() {
    let _this = this
    app.formPost('/api/wx/student/dashboard/index', null).then(res => {
      _this.setData({
        spinShow: false
      });
      wx.stopPullDownRefresh()
      if (res.code === 1) {
        _this.setData({
          fixedPaper: res.response.fixedPaper,
          timeLimitPaper: res.response.timeLimitPaper,
          pushPaper: res.response.pushPaper
        });
      }
    }).catch(e => {
      _this.setData({
        spinShow: false
      });
      app.message(e, 'error')
    })

    app.formPost('/api/wx/student/dashboard/task', null).then(res => {
      _this.setData({
        spinShow: false
      });
      wx.stopPullDownRefresh()
      if (res.code === 1) {
        _this.setData({
          taskList: res.response,
        });
      }
    }).catch(e => {
      _this.setData({
        spinShow: false
      });
      app.message(e, 'error')
    })
  }
})