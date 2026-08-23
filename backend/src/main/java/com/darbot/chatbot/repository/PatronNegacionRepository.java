package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.PatronNegacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatronNegacionRepository extends JpaRepository<PatronNegacion, Long> {
    List<PatronNegacion> findByActivaTrueOrderByPrioridadDesc();
}
