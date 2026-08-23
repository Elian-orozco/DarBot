package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.institucional.entity.Servicio;
import com.darbot.institucional.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServicioHandler implements IntencionHandler {

    private final ServicioRepository servicioRepository;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("ServicioHandler - elementoNegado: '{}'", elementoNegado);
        
        List<Servicio> servicios = servicioRepository.findByActivoTrue();
        
        if (servicios == null || servicios.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_SERVICIOS", 
                "No hay servicios disponibles en este momento.");
        }

        // Aplicar filtro de negación
        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            if (elementoLower.contains("servicio") || elementoLower.equals("servicios")) {
                return new ResultadoChatbot("CONSULTAR_SERVICIOS", 
                    "No hay servicios disponibles (excluyendo: " + elementoNegado + ").");
            }
            servicios = servicios.stream()
                .filter(s -> {
                    String nombre = s.getNombre() != null ? s.getNombre().toLowerCase() : "";
                    String descripcion = s.getDescripcion() != null ? s.getDescripcion().toLowerCase() : "";
                    return !nombre.contains(elementoLower) && !descripcion.contains(elementoLower);
                })
                .collect(Collectors.toList());
        }

        if (servicios.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_SERVICIOS", 
                "No encontré servicios que coincidan con tu búsqueda.");
        }

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_SERVICIOS",
            "La institución ofrece los siguientes servicios:");

        for (Servicio s : servicios) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", s.getNombre());
            item.put("descripcion", s.getDescripcion());
            item.put("horario", s.getHorario());
            item.put("icono", s.getIcono());
            resultado.getResultados().add(item);
        }

        resultado.setOpciones(Arrays.asList("Ver todos los servicios", "Contactar para más información"));
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_SERVICIOS";
    }
}
