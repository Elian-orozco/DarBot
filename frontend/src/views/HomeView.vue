<template>
  <div class="animate-fade-in">
    <!-- Hero Section -->
    <section class="text-center py-16 px-4">
      <div class="max-w-3xl mx-auto">
        <div class="inline-block bg-primary/10 text-primary px-4 py-1 rounded-full text-sm font-medium mb-4">
          🎓 Institución Educativa Darío Torregroza Pérez
        </div>
        <h1 class="text-4xl md:text-5xl font-bold text-gray-900 mb-6">
          Asistente inteligente para tu
          <span class="text-primary">comunidad educativa</span>
        </h1>
        <p class="text-lg text-gray-600 mb-8 max-w-2xl mx-auto">
          DarBot te ayuda con información institucional, horarios, eventos y más. 
          Disponible 24/7 para estudiantes, docentes y padres.
        </p>
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button class="bg-primary hover:bg-primary-dark text-white px-8 py-3 rounded-xl font-medium transition-all duration-200 shadow-md hover:shadow-lg flex items-center justify-center gap-2">
            <span>💬</span>
            Hablar con DarBot
          </button>
          <button class="border-2 border-gray-300 hover:border-primary text-gray-700 hover:text-primary px-8 py-3 rounded-xl font-medium transition-all duration-200">
            Conocer más
          </button>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="grid md:grid-cols-3 gap-6 py-12">
      <div class="bg-white rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow border border-gray-100">
        <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center mb-4">
          <span class="text-2xl">📚</span>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Información académica</h3>
        <p class="text-gray-600 text-sm">Consulta horarios, calificaciones y eventos institucionales.</p>
      </div>
      
      <div class="bg-white rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow border border-gray-100">
        <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center mb-4">
          <span class="text-2xl">🤖</span>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Asistente 24/7</h3>
        <p class="text-gray-600 text-sm">Respuestas inmediatas a tus preguntas sobre la institución.</p>
      </div>
      
      <div class="bg-white rounded-2xl p-6 shadow-sm hover:shadow-md transition-shadow border border-gray-100">
        <div class="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center mb-4">
          <span class="text-2xl">🔒</span>
        </div>
        <h3 class="text-lg font-semibold text-gray-900 mb-2">Seguro y confiable</h3>
        <p class="text-gray-600 text-sm">Tus datos están protegidos con encriptación avanzada.</p>
      </div>
    </section>
  </div>
</template>