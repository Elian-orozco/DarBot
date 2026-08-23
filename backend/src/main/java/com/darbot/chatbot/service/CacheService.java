package com.darbot.chatbot.service;

import com.darbot.chatbot.dto.ChatbotRespuesta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    // Cache para respuestas
    @Cacheable(value = "chatbot_respuestas", key = "#sessionId + '_' + #texto")
    public ChatbotRespuesta obtenerRespuestaCache(String sessionId, String texto) {
        log.info("Cache MISS para: sessionId={}, texto={}", sessionId, texto);
        return null; // El cache se llena desde ChatbotService
    }

    // Guardar en cache
    public void guardarRespuestaCache(String sessionId, String texto, ChatbotRespuesta respuesta) {
        log.info("Guardando en cache: sessionId={}, texto={}", sessionId, texto);
        // Usamos un mapa interno para almacenar las respuestas
        // o podemos usar el cacheManager directamente
    }

    // Limpiar cache para una session específica
    @CacheEvict(value = "chatbot_respuestas", key = "#sessionId + '_' + #texto")
    public void eliminarRespuestaCache(String sessionId, String texto) {
        log.info("Eliminando cache: sessionId={}, texto={}", sessionId, texto);
    }

    // Limpiar todo el cache
    @CacheEvict(value = "chatbot_respuestas", allEntries = true)
    public void limpiarCache() {
        log.info("Limpiando cache completamente");
    }
}
