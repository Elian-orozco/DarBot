package com.darbot.institucional.dto;

import com.darbot.institucional.entity.Contacto;

public record ContactoResponse(Long id, AreaResponse area, String tipo, String valor, String descripcion, Boolean activo) {
    public static ContactoResponse from(Contacto contacto) {
        return new ContactoResponse(contacto.getId(), AreaResponse.from(contacto.getArea()), contacto.getTipo(), contacto.getValor(), contacto.getDescripcion(), contacto.getActivo());
    }
}
