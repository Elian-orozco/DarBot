package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SedeHandler implements IntencionHandler {

    private final SedeRepository sedeRepository;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("SedeHandler - elementoNegado: '{}'", elementoNegado);
        
        List<Sede> sedes = sedeRepository.findByActivaTrue();
        
        if (sedes == null || sedes.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_SEDES", 
                "No hay sedes registradas en el sistema.");
        }

        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            
            if (elementoLower.contains("sede") || elementoLower.equals("sedes") || 
                elementoLower.contains("ubicacion") || elementoLower.contains("ubicación")) {
                return new ResultadoChatbot("CONSULTAR_SEDES", 
                    "No hay sedes disponibles (excluyendo: " + elementoNegado + ").");
            }
            
            sedes = sedes.stream()
                .filter(s -> {
                    String nombre = s.getNombre() != null ? s.getNombre().toLowerCase() : "";
                    String direccion = s.getDireccion() != null ? s.getDireccion().toLowerCase() : "";
                    return !nombre.contains(elementoLower) && !direccion.contains(elementoLower);
                })
                .collect(Collectors.toList());
                
            if (sedes.isEmpty()) {
                return new ResultadoChatbot("CONSULTAR_SEDES", 
                    "No hay sedes disponibles (excluyendo: " + elementoNegado + ").");
            }
        }

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_SEDES", 
            "Sedes de la institución:");

        for (Sede s : sedes) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", s.getNombre());
            item.put("direccion", s.getDireccion());
            item.put("telefono", s.getTelefono());
            item.put("horarioAtencion", s.getHorarioAtencion());
            resultado.getResultados().add(item);
        }

        resultado.setOpciones(Arrays.asList("Ver todas las sedes", "Ver sedes por ubicación"));
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_SEDES";
    }
}
