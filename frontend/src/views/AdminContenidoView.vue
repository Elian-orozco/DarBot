<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Header -->
    <header class="bg-white shadow">
      <div class="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
        <div class="flex items-center gap-4">
          <router-link to="/dashboard" class="text-gray-600 hover:text-gray-800">
            ← Volver
          </router-link>
          <h1 class="text-xl font-bold text-blue-600">📰 Gestión de Contenido</h1>
        </div>
        <div class="flex items-center gap-4">
          <span class="text-gray-600 text-sm">{{ authStore.user?.username }}</span>
          <button @click="authStore.logout" class="text-red-500 hover:text-red-700 text-sm">
            Cerrar sesión
          </button>
        </div>
      </div>
    </header>

    <!-- Contenido -->
    <main class="max-w-7xl mx-auto px-4 py-8">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Noticias -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold flex items-center gap-2">
            📰 Noticias
            <span class="text-sm text-gray-500">{{ noticias.length }}</span>
          </h3>
          <p class="text-sm text-gray-500 mt-2">Gestionar noticias institucionales</p>
          <button class="mt-4 text-blue-600 hover:text-blue-800 text-sm">+ Agregar noticia</button>
        </div>

        <!-- Eventos -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold flex items-center gap-2">
            📅 Eventos
            <span class="text-sm text-gray-500">{{ eventos.length }}</span>
          </h3>
          <p class="text-sm text-gray-500 mt-2">Gestionar eventos y actividades</p>
          <button class="mt-4 text-blue-600 hover:text-blue-800 text-sm">+ Agregar evento</button>
        </div>

        <!-- Documentos -->
        <div class="bg-white rounded-lg shadow p-6">
          <h3 class="text-lg font-semibold flex items-center gap-2">
            📄 Documentos
            <span class="text-sm text-gray-500">{{ documentos.length }}</span>
          </h3>
          <p class="text-sm text-gray-500 mt-2">Gestionar documentos y descargas</p>
          <button class="mt-4 text-blue-600 hover:text-blue-800 text-sm">+ Agregar documento</button>
        </div>
      </div>

      <!-- Lista de noticias -->
      <div class="mt-8 bg-white rounded-lg shadow p-6">
        <h3 class="font-semibold mb-4">Últimas noticias</h3>
        <div v-if="noticias.length === 0" class="text-gray-500 text-sm">
          No hay noticias publicadas.
        </div>
        <div v-for="noticia in noticias" :key="noticia.id" class="border-b border-gray-100 py-3 last:border-0">
          <p class="font-medium">{{ noticia.titulo }}</p>
          <p class="text-sm text-gray-500">{{ noticia.resumen }}</p>
          <span class="text-xs text-gray-400">{{ formatFecha(noticia.fechaPublicacion) }}</span>
        </div>
      </div>
    </main>

    <!-- Chat Widget -->
    <ChatWidget />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import ChatWidget from '../components/chatbot/ChatWidget.vue'
import api from '../services/api'

const authStore = useAuthStore()
const noticias = ref([])
const eventos = ref([])
const documentos = ref([])

function formatFecha(fecha) {
  if (!fecha) return ''
  const d = new Date(fecha)
  return d.toLocaleDateString('es-CO', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

async function cargarContenido() {
  try {
    const [noticiasRes, eventosRes, documentosRes] = await Promise.all([
      api.get('/api/contenidos/noticias'),
      api.get('/api/contenidos/eventos'),
      api.get('/api/contenidos/documentos')
    ])
    noticias.value = noticiasRes.data || []
    eventos.value = eventosRes.data || []
    documentos.value = documentosRes.data || []
  } catch (error) {
    console.error('Error cargando contenido:', error)
  }
}

onMounted(() => {
  cargarContenido()
})
</script>
