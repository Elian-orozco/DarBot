package com.darbot.institucional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContactoRequest(@NotNull Long areaId, @NotBlank @Size(max = 50) String tipo,
                              @NotBlank @Size(max = 255) String valor, @Size(max = 255) String descripcion, Boolean activo) {}
