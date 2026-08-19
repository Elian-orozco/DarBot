package com.darbot.institucional.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "informacion_institucional")
public class InformacionInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String historia;

    @Column(columnDefinition = "TEXT")
    private String mision;

    @Column(columnDefinition = "TEXT")
    private String vision;

    @Column(columnDefinition = "TEXT")
    private String valores;

    @Column(columnDefinition = "TEXT")
    private String filosofia;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;
}