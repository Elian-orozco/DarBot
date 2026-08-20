package com.darbot.chatbot.controller;

import com.darbot.chatbot.service.ChatbotService;
import com.darbot.chatbot.dto.ChatbotPreguntaRequest;
import com.darbot.chatbot.dto.ChatbotRespuestaResponse;
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
    public ResponseEntity<ChatbotRespuestaResponse> hacerPregunta(@Valid @RequestBody ChatbotPreguntaRequest request) {
        String respuesta = chatbotService.procesarMensaje(request.sessionId(), request.mensaje());

        return ResponseEntity.ok(new ChatbotRespuestaResponse(respuesta));
    }
}
