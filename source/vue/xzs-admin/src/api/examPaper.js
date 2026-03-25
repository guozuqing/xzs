import { post } from '@/utils/request'

export default {
  list: () => post('/api/admin/exam/paper/list'),
  publish: query => post('/api/admin/exam/paper/publish', query),
  deletePaper: id => post('/api/admin/exam/paper/delete/' + id)
}
