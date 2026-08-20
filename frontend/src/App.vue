<template>
  <div class="min-h-screen bg-gray-50 font-sans antialiased flex flex-col">
    <Navbar />
    <main class="container mx-auto px-4 py-6 max-w-6xl flex-1">
      <HomeView />
    </main>
    <Footer />
    <ChatInterface />
  </div>
</template>

<script setup>
import Navbar from './components/layout/Navbar.vue'
import Footer from './components/layout/Footer.vue'
import HomeView from './views/HomeView.vue'
import ChatInterface from './components/chat/ChatInterface.vue'
</script>