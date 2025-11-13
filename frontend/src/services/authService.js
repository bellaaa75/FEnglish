import axios from 'axios'

export default {
  userLogin: (data) => axios.post('/api/user/login', data),
  userRegister: (data) => axios.post('/api/user/register', data),
  adminLogin: (data) => axios.post('/api/admin/login', data),
  adminRegister: (data) => axios.post('/api/admin/register', data)
}