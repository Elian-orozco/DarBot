package com.darbot.chatbot.controller;

import com.darbot.chatbot.dto.FeedbackRequest;
import com.darbot.chatbot.service.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Void> registrarFeedback(
            @Valid @RequestBody FeedbackRequest request,
            HttpServletRequest servletRequest) {

        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");

        feedbackService.registrarFeedback(
            request.getSessionId(),
            request.getMensajeId(),
            request.getCalificacion(),
            request.getComentario(),
            ip,
            userAgent
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        return ResponseEntity.ok(feedbackService.obtenerEstadisticas());
    }
}
