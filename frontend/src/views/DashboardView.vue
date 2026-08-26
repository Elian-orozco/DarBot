<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Header -->
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-blue-600">DarBot - Dashboard</h1>
        <div class="flex items-center gap-4">
          <span class="text-gray-600">{{ authStore.user?.username }}</span>
          <button 
            @click="authStore.logout" 
            class="text-red-500 hover:text-red-700 text-sm"
          >
            Cerrar sesión
          </button>
        </div>
      </div>
    </header>

    <!-- Contenido -->
    <main class="max-w-7xl mx-auto px-4 py-8">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Tarjeta 1: Total de preguntas -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-gray-500 text-sm font-medium">Total de preguntas</h3>
          <p class="text-3xl font-bold mt-2">{{ estadisticas.total || 0 }}</p>
        </div>

        <!-- Tarjeta 2: Feedback positivo -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-gray-500 text-sm font-medium">Feedback positivo</h3>
          <p class="text-3xl font-bold mt-2 text-green-600">{{ estadisticas.positivos || 0 }}</p>
        </div>

        <!-- Tarjeta 3: Tasa de aprobación -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-gray-500 text-sm font-medium">Tasa de aprobación</h3>
          <p class="text-3xl font-bold mt-2 text-blue-600">{{ estadisticas.tasa || 0 }}%</p>
        </div>
      </div>

      <!-- Acceso rápido -->
      <div class="mt-8">
        <div class="bg-white rounded-lg shadow p-6">
          <h2 class="text-lg font-semibold mb-4">Acceso rápido</h2>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <router-link 
              to="/chat" 
              class="bg-blue-50 text-blue-600 p-4 rounded-lg text-center hover:bg-blue-100 transition-colors"
            >
              💬 Chat
            </router-link>
            <router-link 
              to="/admin/faq" 
              class="bg-green-50 text-green-600 p-4 rounded-lg text-center hover:bg-green-100 transition-colors"
            >
              📋 FAQ
            </router-link>
            <router-link 
              to="/admin/contenido" 
              class="bg-purple-50 text-purple-600 p-4 rounded-lg text-center hover:bg-purple-100 transition-colors"
            >
              📰 Contenido
            </router-link>
            <router-link 
              to="/admin/analitica" 
              class="bg-orange-50 text-orange-600 p-4 rounded-lg text-center hover:bg-orange-100 transition-colors"
            >
              📊 Analítica
            </router-link>
            <router-link to="/admin/intenciones" class="bg-indigo-50 text-indigo-600 p-4 rounded-lg text-center hover:bg-indigo-100 transition-colors">
              🧠 Intenciones
            </router-link>
            <router-link to="/admin/sinonimos" class="bg-pink-50 text-pink-600 p-4 rounded-lg text-center hover:bg-pink-100 transition-colors">
              🔤 Sinónimos
            </router-link>
          </div>
        </div>
      </div>
    </main>

    <!-- Chat Widget Flotante -->
    <ChatWidget />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { chatbotService } from '../services/chatbot'
import ChatWidget from '../components/chatbot/ChatWidget.vue'

const authStore = useAuthStore()
const estadisticas = ref({})

onMounted(async () => {
  try {
    const data = await chatbotService.obtenerEstadisticas()
    estadisticas.value = {
      total: data.total_feedback || 0,
      positivos: data.positivos || 0,
      tasa: Math.round(data.tasa_aprobacion || 0)
    }
  } catch (error) {
    console.error('Error obteniendo estadísticas:', error)
  }
})
</script>
