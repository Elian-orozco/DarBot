package com.darbot.chatbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "contexto_conversacion")
public class ContextoConversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @Column(name = "ultima_intencion", length = 100)
    private String ultimaIntencion;

    @Column(name = "ultima_entidad", length = 255)
    private String ultimaEntidad; // ID o nombre de la entidad consultada

    @Column(name = "ultima_pregunta", columnDefinition = "TEXT")
    private String ultimaPregunta;

    @Column(name = "ultima_respuesta", columnDefinition = "TEXT")
    private String ultimaRespuesta;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}