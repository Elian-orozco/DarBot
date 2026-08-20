package com.darbot.contenidos.dto;

import com.darbot.contenidos.entity.Documento;
import java.time.LocalDateTime;

public record DocumentoResponse(Long id, String titulo, String descripcion, String nombreArchivo, String rutaArchivo,
                                String tipo, LocalDateTime fechaPublicacion, String estado, LocalDateTime fechaCreacion) {
    public static DocumentoResponse from(Documento documento) {
        return new DocumentoResponse(documento.getId(), documento.getTitulo(), documento.getDescripcion(), documento.getNombreArchivo(),
                documento.getRutaArchivo(), documento.getTipo(), documento.getFechaPublicacion(), documento.getEstado(), documento.getFechaCreacion());
    }
}
