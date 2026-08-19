package com.darbot.institucional.service;

import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Sede> obtenerPorId(Long id) {
        return sedeRepository.findById(id);
    }

    public Sede guardar(Sede sede) {
        return sedeRepository.save(sede);
    }

    public void eliminar(Long id) {
        sedeRepository.deleteById(id);
    }
    
}