//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    spinShow: false,
    timeLimitPaper: [],
    taskList: [],
    examConfigList: []
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
  goGeneratePaper: function(e) {
    let item = e.currentTarget.dataset.item
    let _this = this
    wx.showLoading({ title: '正在组卷', mask: true })
    app.jsonPost('/api/wx/student/dashboard/generatePaper', item).then(res => {
      wx.hideLoading()
      if (res.code === 1) {
        let paperId = res.response.id
        wx.navigateTo({ url: '/pages/exam/do/index?id=' + paperId })
      } else {
        app.message(res.message, 'error')
      }
    }).catch(e => {
      wx.hideLoading()
      app.message(e, 'error')
    })
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
          timeLimitPaper: res.response.timeLimitPaper
        });
      }
    }).catch(e => {
      _this.setData({
        spinShow: false
      });
      app.message(e, 'error')
    })

    app.formPost('/api/wx/student/dashboard/examConfig', null).then(res => {
      if (res.code === 1) {
        _this.setData({
          examConfigList: res.response.examItems || []
        });
      }
    }).catch(e => {
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