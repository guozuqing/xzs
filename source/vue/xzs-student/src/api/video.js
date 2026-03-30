import { post } from '@/utils/request'

export default {
  list: () => {
    return post('/api/student/video/list', {})
  },
  listBySubject: (subjectId) => {
    return post('/api/student/video/listBySubject/' + subjectId, {})
  },
  select: (id) => {
    return post('/api/student/video/select/' + id, {})
  }
}
