package com.darbot.chatbot.repository;

import com.darbot.chatbot.entity.FeedbackChatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackChatbotRepository extends JpaRepository<FeedbackChatbot, Long> {

    List<FeedbackChatbot> findByConversacionId(Long conversacionId);
    
    List<FeedbackChatbot> findByMensajeId(Long mensajeId);

    @Query("SELECT COUNT(f) FROM FeedbackChatbot f WHERE f.calificacion = 1")
    long countPositivos();

    @Query("SELECT COUNT(f) FROM FeedbackChatbot f WHERE f.calificacion = -1")
    long countNegativos();

    @Query("SELECT f.calificacion, COUNT(f) FROM FeedbackChatbot f GROUP BY f.calificacion")
    List<Object[]> countByCalificacion();

    @Query("SELECT FUNCTION('DATE', f.fechaCreacion) as fecha, " +
           "COUNT(CASE WHEN f.calificacion = 1 THEN 1 END) as positivos, " +
           "COUNT(CASE WHEN f.calificacion = -1 THEN 1 END) as negativos " +
           "FROM FeedbackChatbot f " +
           "WHERE f.fechaCreacion BETWEEN :inicio AND :fin " +
           "GROUP BY FUNCTION('DATE', f.fechaCreacion) " +
           "ORDER BY fecha DESC")
    List<Object[]> countByFechaBetween(@Param("inicio") LocalDateTime inicio, 
                                        @Param("fin") LocalDateTime fin);
}
