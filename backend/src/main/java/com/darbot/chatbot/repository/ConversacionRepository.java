package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
    Optional<Conversacion> findBySessionId(String sessionId);
}