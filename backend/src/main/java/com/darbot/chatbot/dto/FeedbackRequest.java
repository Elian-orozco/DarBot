package com.darbot.chatbot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequest {
    @NotNull(message = "El sessionId es obligatorio")
    private String sessionId;

    @NotNull(message = "El mensajeId es obligatorio")
    private Long mensajeId;

    @NotNull(message = "La calificación es obligatoria")
    private Integer calificacion; // 1 = útil, -1 = no útil

    private String comentario;
}
