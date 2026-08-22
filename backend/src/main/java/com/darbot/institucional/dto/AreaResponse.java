package com.darbot.institucional.dto;

import com.darbot.institucional.entity.Area;

import java.time.LocalDateTime;

public record AreaResponse(
    Long id,
    String nombre,
    String descripcion,
    Boolean activo,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {
    public static AreaResponse from(Area area) {
        return new AreaResponse(
            area.getId(),
            area.getNombre(),
            area.getDescripcion(),
            area.getActivo(),
            area.getFechaCreacion(),
            area.getFechaActualizacion()
        );
    }
}