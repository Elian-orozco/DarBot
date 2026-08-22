package com.darbot.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "palabras_clave_intencion")
public class PalabraClaveIntencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intencion_id", nullable = false)
    private Intencion intencion;

    @Column(nullable = false, length = 100)
    private String palabra;

    @Column(name = "es_sinonimo")
    private Boolean esSinonimo = false;

    @Column(name = "peso")
    private Integer peso = 1; // 1-10, qué tan importante es esta palabra
}