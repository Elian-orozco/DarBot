package com.darbot.institucional.controller;

import com.darbot.institucional.entity.InformacionInstitucional;
import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.entity.Area;
import com.darbot.institucional.entity.Contacto;
import com.darbot.institucional.service.InformacionInstitucionalService;
import com.darbot.institucional.service.SedeService;
import com.darbot.institucional.service.AreaContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/institucional")
@RequiredArgsConstructor
public class AdminInstitucionalController {

    private final InformacionInstitucionalService infoService;
    private final SedeService sedeService;
    private final AreaContactoService areaContactoService;

    // Guardar o actualizar la información general de la institución
    @PostMapping("/info")
    public ResponseEntity<InformacionInstitucional> guardarInfo(@RequestBody InformacionInstitucional info) {
        return ResponseEntity.ok(infoService.guardarOActualizar(info));
    }

    // Crear o actualizar una sede
    @PostMapping("/sedes")
    public ResponseEntity<Sede> guardarSede(@RequestBody Sede sede) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sedeService.guardar(sede));
    }

    // Crear un área (ej: Rectoría)
    @PostMapping("/areas")
    public ResponseEntity<Area> guardarArea(@RequestBody Area area) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaContactoService.guardarArea(area));
    }

    // Crear un contacto asociado a un área
    @PostMapping("/contactos")
    public ResponseEntity<Contacto> guardarContacto(@RequestBody Contacto contacto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(areaContactoService.guardarContacto(contacto));
    }
}