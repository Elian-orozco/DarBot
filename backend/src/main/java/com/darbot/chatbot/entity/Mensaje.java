package com.darbot.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @Column(nullable = false, length = 20)
    private String tipo; // USER, BOT, SYSTEM

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "intencion_detectada", length = 100)
    private String intencionDetectada;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDateTime.now();
    }
}