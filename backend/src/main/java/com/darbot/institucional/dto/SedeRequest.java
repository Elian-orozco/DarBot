package com.darbot.institucional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record SedeRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede exceder 100 caracteres") String nombre,
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 200, message = "La dirección no puede exceder 200 caracteres") String direccion,
        @Size(max = 50, message = "El teléfono no puede exceder 50 caracteres") String telefono,
        @Size(max = 100, message = "La jornada no puede exceder 100 caracteres") String jornada,
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres") String descripcion,
        @Size(max = 200, message = "El horario de atención no puede exceder 200 caracteres") String horarioAtencion,
        BigDecimal latitud,
        BigDecimal longitud,
        Boolean activa
) {}
