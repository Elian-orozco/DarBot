package com.darbot.institucional.controller;

import com.darbot.institucional.dto.*;
import com.darbot.institucional.entity.InformacionInstitucional;
import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.entity.Area;
import com.darbot.institucional.service.InformacionInstitucionalService;
import com.darbot.institucional.service.SedeService;
import com.darbot.institucional.service.AreaContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/institucional")
@RequiredArgsConstructor
public class AdminInstitucionalController {

    private final InformacionInstitucionalService infoService;
    private final SedeService sedeService;
    private final AreaContactoService areaContactoService;

    // Guardar o actualizar la información general de la institución
    @PostMapping("/info")
    public ResponseEntity<InformacionInstitucionalResponse> guardarInfo(@Valid @RequestBody InformacionInstitucionalRequest request) {
        InformacionInstitucional info = new InformacionInstitucional();
        info.setNombre(request.nombre()); info.setHistoria(request.historia()); info.setMision(request.mision()); info.setVision(request.vision());
        info.setValores(request.valores()); info.setFilosofia(request.filosofia()); info.setDescripcion(request.descripcion()); info.setLogoUrl(request.logoUrl());
        return ResponseEntity.ok(InformacionInstitucionalResponse.from(infoService.guardarOActualizar(info)));
    }

    // Crear o actualizar una sede
    @PostMapping("/sedes")
    public ResponseEntity<SedeResponse> guardarSede(@Valid @RequestBody SedeRequest request) {
        Sede sede = new Sede();
        sede.setNombre(request.nombre()); sede.setDireccion(request.direccion()); sede.setTelefono(request.telefono());
        sede.setJornada(request.jornada()); sede.setDescripcion(request.descripcion());
        if (request.activa() != null) sede.setActiva(request.activa());
        return ResponseEntity.status(HttpStatus.CREATED).body(SedeResponse.from(sedeService.guardar(sede)));
    }

    // Crear un área (ej: Rectoría)
    @PostMapping("/areas")
    public ResponseEntity<AreaResponse> guardarArea(@Valid @RequestBody AreaRequest request) {
        Area area = new Area();
        area.setNombre(request.nombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(AreaResponse.from(areaContactoService.guardarArea(area)));
    }

    // Crear un contacto asociado a un área
    @PostMapping("/contactos")
    public ResponseEntity<ContactoResponse> guardarContacto(@Valid @RequestBody ContactoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ContactoResponse.from(areaContactoService.crearContacto(request)));
    }
}
