// pages/exam/index/index.js
let app = getApp()
Page({
  data: {
    spinShow: false,
    examConfigList: []
  },
  onLoad: function(options) {
    this.setData({ spinShow: true });
    this.loadExamConfig()
  },
  onPullDownRefresh() {
    this.setData({ spinShow: true });
    this.loadExamConfig()
  },
  loadExamConfig: function() {
    let _this = this
    app.formPost('/api/wx/student/dashboard/examConfig', null).then(res => {
      _this.setData({ spinShow: false });
      wx.stopPullDownRefresh()
      if (res.code === 1) {
        _this.setData({ examConfigList: res.response.examItems || [] });
      }
    }).catch(e => {
      _this.setData({ spinShow: false });
      wx.stopPullDownRefresh()
      app.message(e, 'error')
    })
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
  }
})