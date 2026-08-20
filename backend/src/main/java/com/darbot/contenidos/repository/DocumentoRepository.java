package com.darbot.contenidos.repository;

import com.darbot.contenidos.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByEstado(String estado);
}