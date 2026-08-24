import api from './api'

export const chatbotService = {
  async enviarPregunta(sessionId, mensaje) {
    const response = await api.post('/api/chatbot/pregunta', {
      sessionId,
      mensaje
    })
    return response.data
  },

  async enviarFeedback(sessionId, mensajeId, calificacion, comentario) {
    const response = await api.post('/api/chatbot/feedback', {
      sessionId,
      mensajeId,
      calificacion,
      comentario
    })
    return response.data
  },

  async obtenerEstadisticas() {
    const response = await api.get('/api/chatbot/feedback/estadisticas')
    return response.data
  }
}
