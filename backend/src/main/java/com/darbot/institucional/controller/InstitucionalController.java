package com.darbot.institucional.controller;

import com.darbot.institucional.entity.InformacionInstitucional;
import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.entity.Contacto;
import com.darbot.institucional.service.InformacionInstitucionalService;
import com.darbot.institucional.service.SedeService;
import com.darbot.institucional.service.AreaContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institucional")
@RequiredArgsConstructor
public class InstitucionalController {

    private final InformacionInstitucionalService infoService;
    private final SedeService sedeService;
    private final AreaContactoService areaContactoService;

    // 1. Obtener la misión, visión, historia, etc.
    @GetMapping("/info")
    public ResponseEntity<InformacionInstitucional> getInformacion() {
        InformacionInstitucional info = infoService.obtenerInformacion();
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    // 2. Listar sedes activas para el público
    @GetMapping("/sedes")
    public ResponseEntity<List<Sede>> listarSedesActivas() {
        return ResponseEntity.ok(sedeService.obtenerActivas());
    }

    // 3. Listar todos los contactos de la institución
    @GetMapping("/contactos")
    public ResponseEntity<List<Contacto>> listarContactos() {
        return ResponseEntity.ok(areaContactoService.obtenerContactos());
    }
}