package com.darbot.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatbotPreguntaRequest(
        @NotBlank(message = "sessionId es obligatorio") String sessionId,
        @NotBlank(message = "mensaje es obligatorio") String mensaje
) {
}
