package com.darbot.contenidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventoRequest(@NotBlank @Size(max = 200) String titulo, String descripcion, @NotNull LocalDate fecha,
                            LocalTime horaInicio, LocalTime horaFin, @Size(max = 200) String lugar, @Size(max = 20) String estado) {}
