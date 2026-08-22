package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.PalabraClaveIntencion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PalabraClaveIntencionRepository extends JpaRepository<PalabraClaveIntencion, Long> {
    List<PalabraClaveIntencion> findByIntencionId(Long intencionId);
}