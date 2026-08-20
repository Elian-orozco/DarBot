package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByActivaTrue();
}