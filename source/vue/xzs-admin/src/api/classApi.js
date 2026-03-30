import { post } from '@/utils/request'

export default {
  page: query => post('/api/admin/class/page', query),
  list: () => post('/api/admin/class/list', {}),
  select: id => post('/api/admin/class/select/' + id),
  edit: query => post('/api/admin/class/edit', query),
  deleteClass: id => post('/api/admin/class/delete/' + id),
  members: classId => post('/api/admin/class/members/' + classId),
  addMember: query => post('/api/admin/class/members/add', query),
  removeMember: query => post('/api/admin/class/members/remove', query)
}
