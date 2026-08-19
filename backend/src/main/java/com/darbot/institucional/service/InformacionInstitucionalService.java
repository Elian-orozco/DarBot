package com.darbot.institucional.service;

import com.darbot.institucional.entity.InformacionInstitucional;
import com.darbot.institucional.repository.InformacionInstitucionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InformacionInstitucionalService {

    private final InformacionInstitucionalRepository repository;

    public InformacionInstitucional obtenerInformacion() {
        //la info institucional es única (ID 1). Si no existe, retorna una vacía.
        return repository.findAll().stream().findFirst().orElse(null);
    }

    public InformacionInstitucional guardarOActualizar(InformacionInstitucional info) {
        return repository.save(info);
    }
}