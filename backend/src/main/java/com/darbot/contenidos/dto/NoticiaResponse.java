package com.darbot.contenidos.dto;

import com.darbot.contenidos.entity.Noticia;
import java.time.LocalDateTime;

public record NoticiaResponse(Long id, String titulo, String resumen, String contenido, String imagenUrl,
                              LocalDateTime fechaPublicacion, String estado, LocalDateTime fechaCreacion,
                              LocalDateTime fechaActualizacion) {
    public static NoticiaResponse from(Noticia noticia) {
        return new NoticiaResponse(noticia.getId(), noticia.getTitulo(), noticia.getResumen(), noticia.getContenido(),
                noticia.getImagenUrl(), noticia.getFechaPublicacion(), noticia.getEstado(), noticia.getFechaCreacion(), noticia.getFechaActualizacion());
    }
}
