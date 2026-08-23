package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Negacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegacionRepository extends JpaRepository<Negacion, Long> {
    List<Negacion> findByActivaTrue();
}
