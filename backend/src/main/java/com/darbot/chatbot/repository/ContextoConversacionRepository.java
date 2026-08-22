package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.ContextoConversacion;
import com.darbot.chatbot.entity.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContextoConversacionRepository extends JpaRepository<ContextoConversacion, Long> {
    Optional<ContextoConversacion> findByConversacion(Conversacion conversacion);
}