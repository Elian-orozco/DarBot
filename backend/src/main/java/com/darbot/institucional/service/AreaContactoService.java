package com.darbot.institucional.service;

import com.darbot.institucional.entity.Area;
import com.darbot.institucional.entity.Contacto;
import com.darbot.institucional.dto.ContactoRequest;
import com.darbot.common.exception.ResourceNotFoundException;
import com.darbot.institucional.repository.AreaRepository;
import com.darbot.institucional.repository.ContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaContactoService {

    private final AreaRepository areaRepository;
    private final ContactoRepository contactoRepository;

    // Áreas
    public List<Area> obtenerAreas() {
        return areaRepository.findAll();
    }

    public Area guardarArea(Area area) {
        return areaRepository.save(area);
    }

    // Contactos
    public List<Contacto> obtenerContactos() {
        return contactoRepository.findAll();
    }

    public List<Contacto> obtenerContactosPorArea(Long areaId) {
        return contactoRepository.findByAreaId(areaId);
    }

    public Contacto guardarContacto(Contacto contacto) {
        return contactoRepository.save(contacto);
    }

    public Contacto crearContacto(ContactoRequest request) {
        Area area = areaRepository.findById(request.areaId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el área con id " + request.areaId()));
        Contacto contacto = new Contacto();
        contacto.setArea(area);
        contacto.setTipo(request.tipo());
        contacto.setValor(request.valor());
        contacto.setDescripcion(request.descripcion());
        if (request.activo() != null) {
            contacto.setActivo(request.activo());
        }
        return contactoRepository.save(contacto);
    }

    public void eliminarContacto(Long id) {
        contactoRepository.deleteById(id);
    }
}
