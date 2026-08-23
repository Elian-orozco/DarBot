package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.institucional.entity.Contacto;
import com.darbot.institucional.repository.ContactoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactoHandler implements IntencionHandler {

    private final ContactoRepository contactoRepository;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("ContactoHandler - elementoNegado: '{}'", elementoNegado);
        
        List<Contacto> contactos = contactoRepository.findAll();
        
        if (contactos == null || contactos.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_CONTACTOS", 
                "No hay contactos registrados en el sistema.");
        }

        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            
            if (elementoLower.contains("contacto") || elementoLower.equals("contactos") || 
                elementoLower.contains("telefono") || elementoLower.contains("teléfono") || 
                elementoLower.contains("correo") || elementoLower.contains("email")) {
                return new ResultadoChatbot("CONSULTAR_CONTACTOS", 
                    "No hay contactos disponibles (excluyendo: " + elementoNegado + ").");
            }
            
            contactos = contactos.stream()
                .filter(c -> {
                    String valor = c.getValor() != null ? c.getValor().toLowerCase() : "";
                    String descripcion = c.getDescripcion() != null ? c.getDescripcion().toLowerCase() : "";
                    return !valor.contains(elementoLower) && !descripcion.contains(elementoLower);
                })
                .collect(Collectors.toList());
                
            if (contactos.isEmpty()) {
                return new ResultadoChatbot("CONSULTAR_CONTACTOS", 
                    "No hay contactos disponibles (excluyendo: " + elementoNegado + ").");
            }
        }

        if (entidades.containsKey("tipo_contacto")) {
            String tipo = (String) entidades.get("tipo_contacto");
            contactos = contactos.stream()
                .filter(c -> c.getTipo() != null && c.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
        }

        if (contactos.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_CONTACTOS", 
                "No encontré contactos que coincidan con tu búsqueda.");
        }

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_CONTACTOS", 
            "Contactos institucionales:");

        for (Contacto c : contactos) {
            Map<String, Object> item = new HashMap<>();
            String areaNombre = c.getArea() != null && c.getArea().getNombre() != null ? 
                c.getArea().getNombre() : "";
            item.put("area", areaNombre);
            item.put("tipo", c.getTipo());
            item.put("valor", c.getValor());
            item.put("descripcion", c.getDescripcion());
            resultado.getResultados().add(item);
        }

        resultado.setOpciones(Arrays.asList("Ver todos los contactos", "Contactar por área"));
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_CONTACTOS";
    }
}
