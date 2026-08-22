package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Correccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorreccionRepository extends JpaRepository<Correccion, Long> {
    List<Correccion> findByActivaTrue();
}
