package com.darbot.chatbot.controller;

import com.darbot.chatbot.dto.ChatbotPreguntaRequest;
import com.darbot.chatbot.dto.ChatbotRespuesta;
import com.darbot.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/pregunta")
    public ResponseEntity<ChatbotRespuesta> hacerPregunta(@Valid @RequestBody ChatbotPreguntaRequest request) {
        ChatbotRespuesta respuesta = chatbotService.procesarMensaje(request.sessionId(), request.mensaje());
        return ResponseEntity.ok(respuesta);
    }
}
