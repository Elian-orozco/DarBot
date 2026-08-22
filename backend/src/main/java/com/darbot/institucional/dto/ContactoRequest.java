package com.darbot.institucional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactoRequest(
    @NotNull(message = "El área es obligatoria")
    Long areaId,

    @NotBlank(message = "El tipo de contacto es obligatorio")
    @Size(max = 50, message = "El tipo no puede tener más de 50 caracteres")
    String tipo,

    @NotBlank(message = "El valor es obligatorio")
    String valor,

    String descripcion,

    Boolean activo
) {}