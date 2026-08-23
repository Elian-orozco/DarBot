package com.darbot.institucional.repository;

import com.darbot.institucional.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByActivoTrue();
}
