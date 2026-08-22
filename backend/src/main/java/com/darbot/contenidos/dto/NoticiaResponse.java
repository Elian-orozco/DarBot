package com.darbot.contenidos.dto;

import com.darbot.contenidos.entity.Noticia;

import java.time.LocalDateTime;

public record NoticiaResponse(
    Long id,
    String titulo,
    String resumen,
    String contenido,
    String imagenUrl,
    String estado,
    LocalDateTime fechaPublicacion,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {
    public static NoticiaResponse from(Noticia noticia) {
        return new NoticiaResponse(
            noticia.getId(),
            noticia.getTitulo(),
            noticia.getResumen(),
            noticia.getContenido(),
            noticia.getImagenUrl(),
            noticia.getEstado(),
            noticia.getFechaPublicacion(),
            noticia.getFechaCreacion(),
            noticia.getFechaActualizacion()
        );
    }
}