package com.darbot.institucional.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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
    private String descripcion;

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

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "nombre_institucion", length = 200)
    private String nombreInstitucion;

    @Column(name = "telefono_general", length = 50)
    private String telefonoGeneral;

    @Column(name = "correo_general", length = 100)
    private String correoGeneral;

    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
