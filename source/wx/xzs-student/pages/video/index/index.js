// pages/video/index/index.js
const app = getApp()

Page({
  data: {
    spinShow: false,
    videoList: [],
    subjectList: [],
    currentSubjectId: null
  },
  onLoad: function () {
    this.setData({ spinShow: true })
    this.loadSubjects()
    this.loadVideoList()
  },
  onPullDownRefresh () {
    this.setData({ spinShow: true })
    this.loadVideoList()
  },
  loadSubjects: function () {
    let _this = this
    app.formPost('/api/wx/student/education/subject/list', null).then(res => {
      if (res.code === 1) {
        _this.setData({ subjectList: res.response || [] })
      }
    })
  },
  loadVideoList: function () {
    let _this = this
    let url = '/api/wx/student/video/list'
    if (_this.data.currentSubjectId) {
      url = '/api/wx/student/video/listBySubject/' + _this.data.currentSubjectId
    }
    app.formPost(url, null).then(res => {
      _this.setData({ spinShow: false })
      wx.stopPullDownRefresh()
      if (res.code === 1) {
        _this.setData({ videoList: res.response || [] })
      }
    }).catch(e => {
      _this.setData({ spinShow: false })
      wx.stopPullDownRefresh()
      app.message(e, 'error')
    })
  },
  onSubjectTap: function (e) {
    let subjectId = e.currentTarget.dataset.id
    if (this.data.currentSubjectId === subjectId) {
      this.setData({ currentSubjectId: null })
    } else {
      this.setData({ currentSubjectId: subjectId })
    }
    this.setData({ spinShow: true })
    this.loadVideoList()
  },
  goVideoDetail: function (e) {
    let id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/video/detail/index?id=' + id })
  }
})
