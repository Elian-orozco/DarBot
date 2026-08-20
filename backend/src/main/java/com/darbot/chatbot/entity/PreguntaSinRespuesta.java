package com.darbot.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "preguntas_sin_respuesta")
public class PreguntaSinRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pregunta;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "intento_intencion", length = 100)
    private String intentoIntencion;

    @Column(nullable = false)
    private Boolean resuelta = false;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }
}