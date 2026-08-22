package com.darbot.institucional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AreaRequest(
    @NotBlank(message = "El nombre del área es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    String nombre,

    String descripcion,

    Boolean activo
) {}