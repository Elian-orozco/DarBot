package com.darbot.contenidos.controller;

import com.darbot.contenidos.dto.DocumentoResponse;
import com.darbot.contenidos.dto.EventoResponse;
import com.darbot.contenidos.dto.NoticiaResponse;
import com.darbot.contenidos.service.ContenidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contenidos")
@RequiredArgsConstructor
public class ContenidoController {

    private final ContenidoService contenidoService;

    @GetMapping("/noticias")
    public ResponseEntity<List<NoticiaResponse>> listarNoticias() {
        return ResponseEntity.ok(contenidoService.obtenerNoticiasPublicadas().stream().map(NoticiaResponse::from).toList());
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<EventoResponse>> listarEventos() {
        return ResponseEntity.ok(contenidoService.obtenerProximosEventos().stream().map(EventoResponse::from).toList());
    }

    @GetMapping("/documentos")
    public ResponseEntity<List<DocumentoResponse>> listarDocumentos() {
        return ResponseEntity.ok(contenidoService.obtenerDocumentosActivos().stream().map(DocumentoResponse::from).toList());
    }
}
