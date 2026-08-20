package com.darbot.contenidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentoRequest(@NotBlank @Size(max = 200) String titulo, String descripcion,
                               @NotBlank @Size(max = 255) String nombreArchivo, @NotBlank @Size(max = 255) String rutaArchivo,
                               @Size(max = 50) String tipo, @Size(max = 20) String estado) {}
