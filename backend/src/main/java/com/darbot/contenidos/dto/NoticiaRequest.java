package com.darbot.contenidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoticiaRequest(
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    String titulo,

    @Size(max = 500, message = "El resumen no puede tener más de 500 caracteres")
    String resumen,

    @NotBlank(message = "El contenido es obligatorio")
    String contenido,

    String imagenUrl,

    String estado
) {}