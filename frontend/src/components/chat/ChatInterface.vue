<template>
  <div class="fixed bottom-6 right-6 z-50">
    <!-- Botón flotante -->
    <button 
      @click="toggleChat"
      class="w-14 h-14 bg-primary hover:bg-primary-dark rounded-full shadow-lg flex items-center justify-center transition-all duration-300 hover:scale-105 active:scale-95"
      :class="{ 'rotate-45': isOpen }"
    >
      <svg v-if="!isOpen" class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
      </svg>
      <svg v-else class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
      </svg>
    </button>

    <!-- Panel de chat -->
    <Transition 
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 scale-75 translate-y-4"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-75 translate-y-4"
    >
      <div v-if="isOpen" class="absolute bottom-20 right-0 w-[380px] h-[520px] bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden flex flex-col">
        <!-- Header -->
        <div class="bg-primary px-5 py-4 flex items-center justify-between">
          <div class="flex items-center gap-3">
            <div class="w-3 h-3 bg-green-400 rounded-full animate-pulse"></div>
            <span class="text-white font-semibold">Asistente DarBot</span>
          </div>
          <span class="text-white/80 text-xs">En línea</span>
        </div>

        <!-- Messages -->
        <div class="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50" ref="messagesContainer">
          <MessageBubble 
            v-for="msg in messages" 
            :key="msg.id" 
            :message="msg"
          />
          <TypingIndicator v-if="isTyping" />
        </div>

        <!-- Input -->
        <div class="p-3 border-t border-gray-200 bg-white">
          <div class="flex gap-2">
            <input 
              v-model="newMessage"
              @keyup.enter="sendMessage"
              type="text"
              placeholder="Escribe tu mensaje..."
              class="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 focus:border-primary focus:ring-2 focus:ring-primary/20 outline-none transition-all text-sm bg-gray-50"
            />
            <button 
              @click="sendMessage"
              :disabled="!newMessage.trim()"
              class="px-5 py-2.5 bg-primary text-white rounded-xl hover:bg-primary-dark transition-colors disabled:opacity-50 disabled:cursor-not-allowed text-sm font-medium"
            >
              Enviar
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import MessageBubble from './MessageBubble.vue'
import TypingIndicator from './TypingIndicator.vue'

const isOpen = ref(false)
const isTyping = ref(false)
const newMessage = ref('')
const messagesContainer = ref(null)

const messages = ref([
  { 
    id: 1, 
    text: '¡Hola! Soy el asistente de la I.E. Darío Torregroza. ¿En qué puedo ayudarte?', 
    sender: 'bot' 
  }
])

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    scrollToBottom()
  }
}

const sendMessage = async () => {
  if (!newMessage.value.trim()) return
  
  const userMsg = { 
    id: Date.now(), 
    text: newMessage.value, 
    sender: 'user' 
  }
  messages.value.push(userMsg)
  newMessage.value = ''
  await scrollToBottom()
  
  // Simular respuesta del bot
  isTyping.value = true
  setTimeout(() => {
    const botResponses = [
      'Gracias por tu mensaje. Estoy procesando tu consulta.',
      'Excelente pregunta. Déjame revisar esa información.',
      'Claro, puedo ayudarte con eso. Dame un momento.',
      'Interesante consulta. Voy a buscar la mejor respuesta.'
    ]
    const randomResponse = botResponses[Math.floor(Math.random() * botResponses.length)]
    
    messages.value.push({ 
      id: Date.now() + 1, 
      text: randomResponse, 
      sender: 'bot' 
    })
    isTyping.value = false
    scrollToBottom()
  }, 1500)
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.rotate-45 {
  transform: rotate(45deg);
}
</style>