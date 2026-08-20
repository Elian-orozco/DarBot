package com.darbot.institucional.dto;

import com.darbot.institucional.entity.Area;

public record AreaResponse(Long id, String nombre) {
    public static AreaResponse from(Area area) { return new AreaResponse(area.getId(), area.getNombre()); }
}
