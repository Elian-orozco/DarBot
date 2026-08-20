package com.darbot.contenidos.repository;

import com.darbot.contenidos.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findByEstadoOrderByFechaPublicacionDesc(String estado);
}