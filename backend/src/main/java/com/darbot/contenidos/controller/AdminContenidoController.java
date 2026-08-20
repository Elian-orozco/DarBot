package com.darbot.contenidos.controller;

import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.service.ContenidoService;
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
    public ResponseEntity<Noticia> crearNoticia(@RequestBody Noticia noticia) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contenidoService.guardarNoticia(noticia));
    }

    @PostMapping("/eventos")
    public ResponseEntity<Evento> crearEvento(@RequestBody Evento evento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contenidoService.guardarEvento(evento));
    }

    @PostMapping("/documentos")
    public ResponseEntity<Documento> crearDocumento(@RequestBody Documento documento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contenidoService.guardarDocumento(documento));
    }
}