package com.darbot.contenidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventoRequest(
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede tener más de 200 caracteres")
    String titulo,

    String descripcion,

    @NotNull(message = "La fecha es obligatoria")
    LocalDate fecha,

    LocalTime horaInicio,

    LocalTime horaFin,

    String lugar,

    String estado
) {}