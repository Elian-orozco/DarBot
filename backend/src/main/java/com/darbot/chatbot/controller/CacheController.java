package com.darbot.chatbot.controller;

import com.darbot.chatbot.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;
    private final CacheManager cacheManager;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        // Estadísticas de cada cache
        for (String cacheName : new String[]{"chatbot_respuestas", "chatbot_intenciones", "chatbot_faq"}) {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                // Caffeine no expone directamente las estadísticas, pero podemos obtener el native cache
                if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                    var caffeineCache = (com.github.benmanes.caffeine.cache.Cache) cache.getNativeCache();
                    stats.put(cacheName, Map.of(
                        "size", caffeineCache.estimatedSize(),
                        "stats", caffeineCache.stats()
                    ));
                }
            }
        }

        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> limpiarCache() {
        cacheService.limpiarCache();
        return ResponseEntity.ok().build();
    }
}
