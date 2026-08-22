package com.darbot.institucional.service;

import com.darbot.institucional.dto.SedeRequest;
import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.repository.SedeRepository;
import com.darbot.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeRepository sedeRepository;

    public List<Sede> obtenerTodas() {
        return sedeRepository.findAll();
    }

    public List<Sede> obtenerActivas() {
        return sedeRepository.findByActivaTrue();
    }

    public Sede obtenerPorId(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede no encontrada con id: " + id));
    }

    @Transactional
    public Sede crear(SedeRequest request) {
        Sede sede = new Sede();
        sede.setNombre(request.nombre());
        sede.setDireccion(request.direccion());
        sede.setTelefono(request.telefono());
        sede.setJornada(request.jornada());
        sede.setDescripcion(request.descripcion());
        sede.setHorarioAtencion(request.horarioAtencion());
        sede.setLatitud(request.latitud());
        sede.setLongitud(request.longitud());
        sede.setActiva(request.activa() != null ? request.activa() : true);
        sede.setFechaCreacion(LocalDateTime.now());
        sede.setFechaActualizacion(LocalDateTime.now());
        return sedeRepository.save(sede);
    }

    @Transactional
    public Sede actualizar(Long id, SedeRequest request) {
        Sede sedeExistente = obtenerPorId(id);

        if (request.nombre() != null) sedeExistente.setNombre(request.nombre());
        if (request.direccion() != null) sedeExistente.setDireccion(request.direccion());
        if (request.telefono() != null) sedeExistente.setTelefono(request.telefono());
        if (request.jornada() != null) sedeExistente.setJornada(request.jornada());
        if (request.descripcion() != null) sedeExistente.setDescripcion(request.descripcion());
        if (request.horarioAtencion() != null) sedeExistente.setHorarioAtencion(request.horarioAtencion());
        if (request.latitud() != null) sedeExistente.setLatitud(request.latitud());
        if (request.longitud() != null) sedeExistente.setLongitud(request.longitud());
        if (request.activa() != null) sedeExistente.setActiva(request.activa());

        sedeExistente.setFechaActualizacion(LocalDateTime.now());
        return sedeRepository.save(sedeExistente);
    }

    @Transactional
    public void eliminar(Long id) {
        Sede sede = obtenerPorId(id);
        sedeRepository.delete(sede);
    }

    @Transactional
    public void desactivar(Long id) {
        Sede sede = obtenerPorId(id);
        sede.setActiva(false);
        sede.setFechaActualizacion(LocalDateTime.now());
        sedeRepository.save(sede);
    }

    @Transactional
    public void activar(Long id) {
        Sede sede = obtenerPorId(id);
        sede.setActiva(true);
        sede.setFechaActualizacion(LocalDateTime.now());
        sedeRepository.save(sede);
    }
}