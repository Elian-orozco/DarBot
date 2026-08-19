package com.darbot.institucional.repository;

import com.darbot.institucional.entity.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    List<Contacto> findByAreaId(Long areaId);
}