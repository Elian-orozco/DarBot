import api from './api'

export const authService = {
  async login(username, password) {
    const response = await api.post('/api/auth/login', { username, password })
    return response.data
  },

  async register(userData) {
    const response = await api.post('/api/auth/register', userData)
    return response.data
  },

  async getCurrentUser() {
    const response = await api.get('/api/auth/me')
    return response.data
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}
