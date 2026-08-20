package com.darbot.contenidos.dto;

import com.darbot.contenidos.entity.Evento;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record EventoResponse(Long id, String titulo, String descripcion, LocalDate fecha, LocalTime horaInicio,
                             LocalTime horaFin, String lugar, String estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
    public static EventoResponse from(Evento evento) {
        return new EventoResponse(evento.getId(), evento.getTitulo(), evento.getDescripcion(), evento.getFecha(), evento.getHoraInicio(),
                evento.getHoraFin(), evento.getLugar(), evento.getEstado(), evento.getFechaCreacion(), evento.getFechaActualizacion());
    }
}
