<template>
  <header class="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-50">
    <nav class="container mx-auto px-4 py-3 flex items-center justify-between max-w-6xl">
      <!-- Logo -->
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 bg-primary rounded-lg flex items-center justify-center shadow-md">
          <span class="text-white font-bold text-xl">D</span>
        </div>
        <span class="text-xl font-bold text-gray-900 tracking-tight">
          Dar<span class="text-primary">Bot</span>
        </span>
      </div>

      <!-- Navegación Desktop -->
      <div class="hidden md:flex items-center gap-8">
        <a href="#" class="text-sm font-medium text-gray-600 hover:text-primary transition-colors duration-200">
          Inicio
        </a>
        <a href="#" class="text-sm font-medium text-gray-600 hover:text-primary transition-colors duration-200">
          Dashboard
        </a>
        <a href="#" class="text-sm font-medium text-gray-600 hover:text-primary transition-colors duration-200">
          Acerca de
        </a>
        <button class="bg-primary hover:bg-primary-dark text-white px-5 py-2 rounded-lg text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md flex items-center gap-2">
          <span>💬</span>
          Asistente
        </button>
      </div>

      <!-- Mobile Menu Button -->
      <button 
        @click="isMenuOpen = !isMenuOpen"
        class="md:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors"
      >
        <svg class="w-6 h-6 text-gray-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
    </nav>

    <!-- Mobile Menu -->
    <div v-if="isMenuOpen" class="md:hidden bg-white border-t border-gray-200 px-4 py-4 shadow-lg animate-fade-in">
      <div class="flex flex-col gap-3">
        <a href="#" class="text-gray-700 hover:text-primary py-2 transition-colors">Inicio</a>
        <a href="#" class="text-gray-700 hover:text-primary py-2 transition-colors">Dashboard</a>
        <a href="#" class="text-gray-700 hover:text-primary py-2 transition-colors">Acerca de</a>
        <button class="bg-primary text-white px-4 py-2 rounded-lg text-center hover:bg-primary-dark transition-colors">
          💬 Asistente
        </button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'

const isMenuOpen = ref(false)
</script>