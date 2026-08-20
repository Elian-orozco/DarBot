package com.darbot.contenidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoticiaRequest(
        @NotBlank @Size(max = 200) String titulo,
        String resumen,
        @NotBlank String contenido,
        @Size(max = 255) String imagenUrl,
        @Size(max = 20) String estado
) {}
