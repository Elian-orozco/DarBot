package com.darbot.institucional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SedeRequest(@NotBlank @Size(max = 100) String nombre, @NotBlank @Size(max = 200) String direccion,
                          @Size(max = 50) String telefono, @Size(max = 100) String jornada, String descripcion, Boolean activa) {}
