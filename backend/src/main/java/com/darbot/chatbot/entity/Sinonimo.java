package com.darbot.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sinonimos")
public class Sinonimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "palabra_base", nullable = false, length = 100)
    private String palabraBase;

    @Column(nullable = false, length = 100)
    private String sinonimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intencion_id")
    private Intencion intencion;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
