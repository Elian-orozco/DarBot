package com.darbot.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "intenciones")
public class Intencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre; // Ej: CONSULTAR_EVENTO, CONSULTAR_DOCUMENTO

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activa = true;
}