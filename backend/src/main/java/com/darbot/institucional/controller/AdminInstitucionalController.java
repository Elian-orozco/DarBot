package com.darbot.institucional.controller;

import com.darbot.institucional.dto.*;
import com.darbot.institucional.service.InformacionInstitucionalService;
import com.darbot.institucional.service.SedeService;
import com.darbot.institucional.service.AreaContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/institucional")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInstitucionalController {

    private final InformacionInstitucionalService infoService;
    private final SedeService sedeService;
    private final AreaContactoService areaContactoService;

    // ==================== INFORMACIÓN INSTITUCIONAL ====================

    @GetMapping("/info")
    public ResponseEntity<InformacionInstitucionalResponse> obtenerInfo() {
        var info = infoService.obtenerInformacion();
        if (info == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(InformacionInstitucionalResponse.from(info));
    }

    @PostMapping("/info")
    public ResponseEntity<InformacionInstitucionalResponse> crearInfo(@Valid @RequestBody InformacionInstitucionalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InformacionInstitucionalResponse.from(infoService.crear(request)));
    }

    @PutMapping("/info/{id}")
    public ResponseEntity<InformacionInstitucionalResponse> actualizarInfo(
            @PathVariable Long id,
            @Valid @RequestBody InformacionInstitucionalRequest request) {
        return ResponseEntity.ok(InformacionInstitucionalResponse.from(infoService.actualizar(id, request)));
    }

    @DeleteMapping("/info/{id}")
    public ResponseEntity<Void> eliminarInfo(@PathVariable Long id) {
        infoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/info/guardar-o-actualizar")
    public ResponseEntity<InformacionInstitucionalResponse> guardarOActualizarInfo(@Valid @RequestBody InformacionInstitucionalRequest request) {
        return ResponseEntity.ok(InformacionInstitucionalResponse.from(infoService.guardarOActualizar(request)));
    }

    // ==================== SEDES ====================

    @GetMapping("/sedes")
    public ResponseEntity<List<SedeResponse>> listarSedes() {
        return ResponseEntity.ok(sedeService.obtenerTodas().stream().map(SedeResponse::from).toList());
    }

    @GetMapping("/sedes/{id}")
    public ResponseEntity<SedeResponse> obtenerSede(@PathVariable Long id) {
        return ResponseEntity.ok(SedeResponse.from(sedeService.obtenerPorId(id)));
    }

    @PostMapping("/sedes")
    public ResponseEntity<SedeResponse> crearSede(@Valid @RequestBody SedeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SedeResponse.from(sedeService.crear(request)));
    }

    @PutMapping("/sedes/{id}")
    public ResponseEntity<SedeResponse> actualizarSede(
            @PathVariable Long id,
            @Valid @RequestBody SedeRequest request) {
        return ResponseEntity.ok(SedeResponse.from(sedeService.actualizar(id, request)));
    }

    @DeleteMapping("/sedes/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Long id) {
        sedeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/sedes/{id}/estado")
    public ResponseEntity<SedeResponse> cambiarEstadoSede(
            @PathVariable Long id,
            @RequestParam boolean activa) {
        if (activa) {
            sedeService.activar(id);
        } else {
            sedeService.desactivar(id);
        }
        return ResponseEntity.ok(SedeResponse.from(sedeService.obtenerPorId(id)));
    }

    // ==================== ÁREAS ====================

    @GetMapping("/areas")
    public ResponseEntity<List<AreaResponse>> listarAreas() {
        return ResponseEntity.ok(areaContactoService.obtenerAreas().stream().map(AreaResponse::from).toList());
    }

    @GetMapping("/areas/{id}")
    public ResponseEntity<AreaResponse> obtenerArea(@PathVariable Long id) {
        return ResponseEntity.ok(AreaResponse.from(areaContactoService.obtenerAreaPorId(id)));
    }

    @PostMapping("/areas")
    public ResponseEntity<AreaResponse> crearArea(@Valid @RequestBody AreaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AreaResponse.from(areaContactoService.crearArea(request)));
    }

    @PutMapping("/areas/{id}")
    public ResponseEntity<AreaResponse> actualizarArea(
            @PathVariable Long id,
            @Valid @RequestBody AreaRequest request) {
        return ResponseEntity.ok(AreaResponse.from(areaContactoService.actualizarArea(id, request)));
    }

    @DeleteMapping("/areas/{id}")
    public ResponseEntity<Void> eliminarArea(@PathVariable Long id) {
        areaContactoService.eliminarArea(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== CONTACTOS ====================

    @GetMapping("/contactos")
    public ResponseEntity<List<ContactoResponse>> listarContactos() {
        return ResponseEntity.ok(areaContactoService.obtenerContactos().stream().map(ContactoResponse::from).toList());
    }

    @GetMapping("/contactos/{id}")
    public ResponseEntity<ContactoResponse> obtenerContacto(@PathVariable Long id) {
        return ResponseEntity.ok(ContactoResponse.from(areaContactoService.obtenerContactoPorId(id)));
    }

    @GetMapping("/areas/{areaId}/contactos")
    public ResponseEntity<List<ContactoResponse>> listarContactosPorArea(@PathVariable Long areaId) {
        return ResponseEntity.ok(areaContactoService.obtenerContactosPorArea(areaId).stream()
                .map(ContactoResponse::from)
                .toList());
    }

    @PostMapping("/contactos")
    public ResponseEntity<ContactoResponse> crearContacto(@Valid @RequestBody ContactoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ContactoResponse.from(areaContactoService.crearContacto(request)));
    }

    @PutMapping("/contactos/{id}")
    public ResponseEntity<ContactoResponse> actualizarContacto(
            @PathVariable Long id,
            @Valid @RequestBody ContactoRequest request) {
        return ResponseEntity.ok(ContactoResponse.from(areaContactoService.actualizarContacto(id, request)));
    }

    @DeleteMapping("/contactos/{id}")
    public ResponseEntity<Void> eliminarContacto(@PathVariable Long id) {
        areaContactoService.eliminarContacto(id);
        return ResponseEntity.noContent().build();
    }
}