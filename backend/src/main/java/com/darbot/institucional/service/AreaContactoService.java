package com.darbot.institucional.service;

import com.darbot.institucional.dto.AreaRequest;
import com.darbot.institucional.dto.ContactoRequest;
import com.darbot.institucional.entity.Area;
import com.darbot.institucional.entity.Contacto;
import com.darbot.common.exception.ResourceNotFoundException;
import com.darbot.institucional.repository.AreaRepository;
import com.darbot.institucional.repository.ContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaContactoService {

    private final AreaRepository areaRepository;
    private final ContactoRepository contactoRepository;

    // ==================== ÁREAS ====================

    public List<Area> obtenerAreas() {
        return areaRepository.findAll();
    }

    public Area obtenerAreaPorId(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con id: " + id));
    }

    @Transactional
    public Area crearArea(AreaRequest request) {
        Area area = new Area();
        area.setNombre(request.nombre());
        area.setDescripcion(request.descripcion());
        area.setActivo(request.activo() != null ? request.activo() : true);
        area.setFechaCreacion(LocalDateTime.now());
        area.setFechaActualizacion(LocalDateTime.now());
        return areaRepository.save(area);
    }

    @Transactional
    public Area actualizarArea(Long id, AreaRequest request) {
        Area areaExistente = obtenerAreaPorId(id);

        if (request.nombre() != null) areaExistente.setNombre(request.nombre());
        if (request.descripcion() != null) areaExistente.setDescripcion(request.descripcion());
        if (request.activo() != null) areaExistente.setActivo(request.activo());

        areaExistente.setFechaActualizacion(LocalDateTime.now());
        return areaRepository.save(areaExistente);
    }

    @Transactional
    public void eliminarArea(Long id) {
        Area area = obtenerAreaPorId(id);
        areaRepository.delete(area);
    }

    // ==================== CONTACTOS ====================

    public List<Contacto> obtenerContactos() {
        return contactoRepository.findAll();
    }

    public List<Contacto> obtenerContactosPorArea(Long areaId) {
        return contactoRepository.findByAreaId(areaId);
    }

    public Contacto obtenerContactoPorId(Long id) {
        return contactoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con id: " + id));
    }

    @Transactional
    public Contacto crearContacto(ContactoRequest request) {
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el área con id " + request.areaId()));

        Contacto contacto = new Contacto();
        contacto.setArea(area);
        contacto.setTipo(request.tipo());
        contacto.setValor(request.valor());
        contacto.setDescripcion(request.descripcion());
        contacto.setActivo(request.activo() != null ? request.activo() : true);
        contacto.setFechaCreacion(LocalDateTime.now());
        contacto.setFechaActualizacion(LocalDateTime.now());

        return contactoRepository.save(contacto);
    }

    @Transactional
    public Contacto actualizarContacto(Long id, ContactoRequest request) {
        Contacto contactoExistente = obtenerContactoPorId(id);

        if (request.tipo() != null) contactoExistente.setTipo(request.tipo());
        if (request.valor() != null) contactoExistente.setValor(request.valor());
        if (request.descripcion() != null) contactoExistente.setDescripcion(request.descripcion());
        if (request.activo() != null) contactoExistente.setActivo(request.activo());

        if (request.areaId() != null) {
            Area area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe el área con id " + request.areaId()));
            contactoExistente.setArea(area);
        }

        contactoExistente.setFechaActualizacion(LocalDateTime.now());
        return contactoRepository.save(contactoExistente);
    }

    @Transactional
    public void eliminarContacto(Long id) {
        Contacto contacto = obtenerContactoPorId(id);
        contactoRepository.delete(contacto);
    }
}