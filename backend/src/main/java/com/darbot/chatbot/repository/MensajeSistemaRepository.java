package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.MensajeSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MensajeSistemaRepository extends JpaRepository<MensajeSistema, Long> {
    Optional<MensajeSistema> findByClave(String clave);
}
