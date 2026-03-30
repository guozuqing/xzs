// pages/exam/edit/index.js
const app = getApp()

Page({
  data: {
    spinShow: false,
    paper: {},
    answer: {}
  },

  onLoad: function(options) {
    let _this = this
    let id = options.id
    _this.setData({ spinShow: true })

    app.formPost('/api/wx/student/exampaper/answer/read/' + id, null).then(res => {
      if (res.code === 1) {
        _this.setData({
          answer: res.response,
          spinShow: false
        })
        app.formPost('/api/wx/student/exampaper/read/' + res.response.examPaperId, null).then(paperRes => {
          if (paperRes.code === 1) {
            _this.setData({
              paper: paperRes.response
            })
          }
        })
      } else {
        _this.setData({ spinShow: false })
        app.message(res.message, 'error')
      }
    }).catch(e => {
      _this.setData({ spinShow: false })
      app.message(e, 'error')
    })
  },

  formSubmit(e) {
    let _this = this
    let formData = e.detail.value
    let answer = _this.data.answer
    let answerItems = answer.answerItems

    answerItems.forEach(item => {
      let scoreKey = item.itemOrder + '_score'
      if (formData[scoreKey] !== undefined && formData[scoreKey] !== '') {
        item.score = parseFloat(formData[scoreKey])
        item.doRight = item.score > 0
      }
    })

    answer.answerItems = answerItems
    _this.setData({ spinShow: true })

    app.formPost('/api/wx/student/exampaper/answer/edit', answer).then(res => {
      _this.setData({ spinShow: false })
      if (res.code === 1) {
        app.message('批改提交成功', 'success')
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      } else {
        app.message(res.message, 'error')
      }
    }).catch(e => {
      _this.setData({ spinShow: false })
      app.message(e, 'error')
    })
  }
})