package com.darbot.chatbot.entity;

import com.darbot.usuarios.entity.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conversaciones")
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Opcional, por si el usuario está logueado

    @Column(length = 20)
    private String estado = "ACTIVA"; // ACTIVA, CERRADA

    @PrePersist
    protected void onCreate() {
        this.fechaInicio = LocalDateTime.now();
    }
}