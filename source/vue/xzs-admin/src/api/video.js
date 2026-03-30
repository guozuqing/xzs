import { post, form } from '@/utils/request'

export default {
  pageList: query => post('/api/admin/video/page', query),
  edit: query => post('/api/admin/video/edit', query),
  select: id => post('/api/admin/video/select/' + id),
  deleteVideo: id => post('/api/admin/video/delete/' + id),
  upload: formData => form('/api/admin/video/upload', formData)
}
