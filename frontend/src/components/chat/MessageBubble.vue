<template>
  <div class="flex" :class="isUser ? 'justify-end' : 'justify-start'">
    <div 
      class="max-w-[80%] px-4 py-2.5 rounded-2xl shadow-sm"
      :class="isUser 
        ? 'bg-primary text-white rounded-br-sm' 
        : 'bg-white text-gray-800 rounded-bl-sm border border-gray-100'"
    >
      <p class="text-sm leading-relaxed">{{ message.text }}</p>
      <span class="text-[10px] opacity-70 mt-1 block" :class="isUser ? 'text-white/70' : 'text-gray-400'">
        {{ formattedTime }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: {
    type: Object,
    required: true,
    validator: (value) => {
      return value.text && value.sender
    }
  }
})

const isUser = computed(() => props.message.sender === 'user')

const formattedTime = computed(() => {
  const date = new Date()
  return date.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' })
})
</script>