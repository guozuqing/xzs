import { post } from '@/utils/request'

export default {
  index: () => post('/api/student/dashboard/index'),
  task: () => post('/api/student/dashboard/task'),
  examConfig: () => post('/api/student/dashboard/examConfig'),
  generatePaper: query => post('/api/student/dashboard/generatePaper', query)
}
