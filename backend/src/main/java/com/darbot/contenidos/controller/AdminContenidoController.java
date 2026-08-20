package com.darbot.contenidos.controller;

import com.darbot.contenidos.dto.*;
import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.service.ContenidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/contenidos")
@RequiredArgsConstructor
public class AdminContenidoController {

    private final ContenidoService contenidoService;

    @PostMapping("/noticias")
    public ResponseEntity<NoticiaResponse> crearNoticia(@Valid @RequestBody NoticiaRequest request) {
        Noticia noticia = new Noticia();
        noticia.setTitulo(request.titulo()); noticia.setResumen(request.resumen()); noticia.setContenido(request.contenido());
        noticia.setImagenUrl(request.imagenUrl());
        if (request.estado() != null) noticia.setEstado(request.estado());
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticiaResponse.from(contenidoService.guardarNoticia(noticia)));
    }

    @PostMapping("/eventos")
    public ResponseEntity<EventoResponse> crearEvento(@Valid @RequestBody EventoRequest request) {
        Evento evento = new Evento();
        evento.setTitulo(request.titulo()); evento.setDescripcion(request.descripcion()); evento.setFecha(request.fecha());
        evento.setHoraInicio(request.horaInicio()); evento.setHoraFin(request.horaFin()); evento.setLugar(request.lugar());
        if (request.estado() != null) evento.setEstado(request.estado());
        return ResponseEntity.status(HttpStatus.CREATED).body(EventoResponse.from(contenidoService.guardarEvento(evento)));
    }

    @PostMapping("/documentos")
    public ResponseEntity<DocumentoResponse> crearDocumento(@Valid @RequestBody DocumentoRequest request) {
        Documento documento = new Documento();
        documento.setTitulo(request.titulo()); documento.setDescripcion(request.descripcion()); documento.setNombreArchivo(request.nombreArchivo());
        documento.setRutaArchivo(request.rutaArchivo()); documento.setTipo(request.tipo());
        if (request.estado() != null) documento.setEstado(request.estado());
        return ResponseEntity.status(HttpStatus.CREATED).body(DocumentoResponse.from(contenidoService.guardarDocumento(documento)));
    }
}
