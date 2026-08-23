package com.darbot.contenidos.repository;

import com.darbot.contenidos.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate fecha);
    List<Evento> findByFechaBetweenOrderByFechaAsc(LocalDate fechaInicio, LocalDate fechaFin);
}
