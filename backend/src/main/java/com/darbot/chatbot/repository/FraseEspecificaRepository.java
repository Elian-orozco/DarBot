package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.FraseEspecifica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FraseEspecificaRepository extends JpaRepository<FraseEspecifica, Long> {
    List<FraseEspecifica> findByActivaTrue();
    Optional<FraseEspecifica> findByFrase(String frase);
}