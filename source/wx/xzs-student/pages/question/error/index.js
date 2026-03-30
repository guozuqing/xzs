// pages/question/error/index.js
const app = getApp()

Page({
  data: {
    spinShow: false,
    queryParam: {
      pageIndex: 1,
      pageSize: 20
    },
    tableData: [],
    total: 0,
    loadMoreTip: '加载更多',
    loadMoreLoad: false,
    showDetailModal: false,
    detailLoading: false,
    selectItem: {
      questionType: 0,
      questionItem: null,
      answerItem: null
    }
  },

  onLoad: function () {
    this.setData({ spinShow: true })
    this.search(true)
  },

  onPullDownRefresh() {
    this.setData({
      spinShow: true,
      ['queryParam.pageIndex']: 1
    })
    this.search(true)
  },

  onReachBottom() {
    if (this.data.tableData.length < this.data.total) {
      this.setData({
        loadMoreLoad: true,
        ['queryParam.pageIndex']: this.data.queryParam.pageIndex + 1
      })
      this.search(false)
    }
  },

  search(init) {
    let _this = this
    app.formPost('/api/wx/student/question/answer/page', this.data.queryParam).then(res => {
      _this.setData({ spinShow: false, loadMoreLoad: false })
      wx.stopPullDownRefresh()
      if (res.code === 1) {
        let list = res.response.list
        if (!init) {
          list = _this.data.tableData.concat(list)
        }
        _this.setData({
          tableData: list,
          total: res.response.total,
          ['queryParam.pageIndex']: res.response.pageNum,
          loadMoreTip: list.length >= res.response.total ? '没有更多了' : '加载更多'
        })
      }
    }).catch(e => {
      _this.setData({ spinShow: false, loadMoreLoad: false })
      wx.stopPullDownRefresh()
      app.message(e, 'error')
    })
  },

  showDetail(e) {
    let id = e.currentTarget.dataset.id
    let _this = this
    _this.setData({ showDetailModal: true, detailLoading: true })
    app.formPost('/api/wx/student/question/answer/select/' + id, null).then(res => {
      _this.setData({ detailLoading: false })
      if (res.code === 1) {
        let response = res.response
        _this.setData({
          'selectItem.questionType': response.questionVM.questionType,
          'selectItem.questionItem': response.questionVM,
          'selectItem.answerItem': response.questionAnswerVM
        })
      }
    }).catch(e => {
      _this.setData({ detailLoading: false })
      app.message(e, 'error')
    })
  },

  closeDetail() {
    this.setData({
      showDetailModal: false,
      'selectItem.questionItem': null,
      'selectItem.answerItem': null
    })
  }
})
