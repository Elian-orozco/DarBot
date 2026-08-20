package com.darbot.institucional.dto;

import com.darbot.institucional.entity.Sede;

public record SedeResponse(Long id, String nombre, String direccion, String telefono, String jornada, String descripcion, Boolean activa) {
    public static SedeResponse from(Sede sede) {
        return new SedeResponse(sede.getId(), sede.getNombre(), sede.getDireccion(), sede.getTelefono(), sede.getJornada(), sede.getDescripcion(), sede.getActiva());
    }
}
