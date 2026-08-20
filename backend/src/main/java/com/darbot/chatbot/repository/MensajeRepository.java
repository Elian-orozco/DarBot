package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByConversacionIdOrderByFechaAsc(Long conversacionId);
}