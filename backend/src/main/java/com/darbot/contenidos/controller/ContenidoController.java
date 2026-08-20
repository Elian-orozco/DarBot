package com.darbot.contenidos.controller;

import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
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
    public ResponseEntity<List<Noticia>> listarNoticias() {
        return ResponseEntity.ok(contenidoService.obtenerNoticiasPublicadas());
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(contenidoService.obtenerProximosEventos());
    }

    @GetMapping("/documentos")
    public ResponseEntity<List<Documento>> listarDocumentos() {
        return ResponseEntity.ok(contenidoService.obtenerDocumentosActivos());
    }
}