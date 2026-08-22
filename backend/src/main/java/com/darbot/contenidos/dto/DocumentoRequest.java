package com.darbot.contenidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentoRequest(
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    String titulo,

    String descripcion,

    @NotBlank(message = "El nombre del archivo es obligatorio")
    String nombreArchivo,

    @NotBlank(message = "La ruta del archivo es obligatoria")
    String rutaArchivo,

    String tipo,

    String estado
) {}