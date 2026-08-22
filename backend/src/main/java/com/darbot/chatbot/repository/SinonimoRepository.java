package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Sinonimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SinonimoRepository extends JpaRepository<Sinonimo, Long> {
    List<Sinonimo> findByActivaTrue();
    List<Sinonimo> findBySinonimo(String sinonimo);
    List<Sinonimo> findByPalabraBase(String palabraBase);
}
