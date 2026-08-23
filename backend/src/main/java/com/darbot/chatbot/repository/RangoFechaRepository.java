package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.RangoFecha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RangoFechaRepository extends JpaRepository<RangoFecha, Long> {
    Optional<RangoFecha> findByNombre(String nombre);
    List<RangoFecha> findByActivoTrue();
}
