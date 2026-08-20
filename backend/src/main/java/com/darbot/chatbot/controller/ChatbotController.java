package com.darbot.chatbot.controller;

import com.darbot.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/pregunta")
    public ResponseEntity<Map<String, String>> hacerPregunta(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String mensaje = request.get("mensaje");

        if (sessionId == null || mensaje == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "sessionId y mensaje son obligatorios"));
        }

        String respuesta = chatbotService.procesarMensaje(sessionId, mensaje);

        return ResponseEntity.ok(Map.of("respuesta", respuesta));
    }
}