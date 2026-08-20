package com.darbot.institucional.controller;

import com.darbot.institucional.dto.InformacionInstitucionalResponse;
import com.darbot.institucional.dto.SedeResponse;
import com.darbot.institucional.dto.ContactoResponse;
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
    public ResponseEntity<InformacionInstitucionalResponse> getInformacion() {
        var info = infoService.obtenerInformacion();
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(InformacionInstitucionalResponse.from(info));
    }

    // 2. Listar sedes activas para el público
    @GetMapping("/sedes")
    public ResponseEntity<List<SedeResponse>> listarSedesActivas() {
        return ResponseEntity.ok(sedeService.obtenerActivas().stream().map(SedeResponse::from).toList());
    }

    // 3. Listar todos los contactos de la institución
    @GetMapping("/contactos")
    public ResponseEntity<List<ContactoResponse>> listarContactos() {
        return ResponseEntity.ok(areaContactoService.obtenerContactos().stream().map(ContactoResponse::from).toList());
    }
}
