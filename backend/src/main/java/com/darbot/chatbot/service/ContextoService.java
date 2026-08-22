package com.darbot.chatbot.service;

import com.darbot.chatbot.entity.Conversacion;
import com.darbot.chatbot.entity.ContextoConversacion;
import com.darbot.chatbot.repository.ContextoConversacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContextoService {

    private final ContextoConversacionRepository contextoRepository;

    public Optional<ContextoConversacion> obtenerContexto(Conversacion conversacion) {
        return contextoRepository.findByConversacion(conversacion);
    }

    public ContextoConversacion crearContexto(Conversacion conversacion) {
        ContextoConversacion contexto = new ContextoConversacion();
        contexto.setConversacion(conversacion);
        contexto.setFechaActualizacion(LocalDateTime.now());
        return contextoRepository.save(contexto);
    }

    public ContextoConversacion actualizarContexto(
            Conversacion conversacion,
            String intencion,
            String entidad,
            String pregunta,
            String respuesta) {
        
        ContextoConversacion contexto = obtenerContexto(conversacion)
                .orElseGet(() -> crearContexto(conversacion));

        if (intencion != null) contexto.setUltimaIntencion(intencion);
        if (entidad != null) contexto.setUltimaEntidad(entidad);
        if (pregunta != null) contexto.setUltimaPregunta(pregunta);
        if (respuesta != null) contexto.setUltimaRespuesta(respuesta);
        
        contexto.setFechaActualizacion(LocalDateTime.now());
        return contextoRepository.save(contexto);
    }

    public boolean esPreguntaDeContexto(String texto) {
        // Palabras que indican que la pregunta se refiere al contexto anterior
        String[] indicadores = {
            "y eso", "y esa", "y el", "y la", 
            "ese", "esa", "eso", "eso que", 
            "a qué hora", "cuándo", "dónde", 
            "y cómo", "y por qué"
        };
        String textoLower = texto.toLowerCase();
        for (String ind : indicadores) {
            if (textoLower.contains(ind)) {
                return true;
            }
        }
        return false;
    }
}