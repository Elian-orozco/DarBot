package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Intencion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IntencionRepository extends JpaRepository<Intencion, Long> {
    Optional<Intencion> findByNombre(String nombre);
}