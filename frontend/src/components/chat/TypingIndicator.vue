<template>
  <div class="flex justify-start">
    <div class="bg-white px-4 py-3 rounded-2xl rounded-bl-sm border border-gray-100 shadow-sm">
      <div class="flex gap-1.5">
        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0s"></span>
        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.2s"></span>
        <span class="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style="animation-delay: 0.4s"></span>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}
.animate-bounce {
  animation: bounce 1s ease-in-out infinite;
}
</style>