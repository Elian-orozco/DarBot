package com.darbot.institucional.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "contactos")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(nullable = false, length = 50)
    private String tipo; // Ej: Telefono, Correo, Horario

    @Column(nullable = false, length = 255)
    private String valor; // El dato en sí (ej: 3001234567 o sec@colegio.edu.co)

    @Column(length = 255)
    private String descripcion; // Detalle adicional

    @Column(nullable = false)
    private Boolean activo = true;
}