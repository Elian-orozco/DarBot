package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoticiaHandler implements IntencionHandler {

    private final NoticiaRepository noticiaRepository;
    private static final int MAX_RESULTADOS = 5;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("NoticiaHandler - elementoNegado: '{}'", elementoNegado);
        
        List<Noticia> noticias = noticiaRepository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADA");
        
        if (noticias == null || noticias.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_NOTICIAS", 
                "No hay novedades publicadas en este momento.");
        }

        // Aplicar filtro de negación
        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            
            if (elementoLower.contains("noticia") || elementoLower.equals("noticias") || 
                elementoLower.contains("novedad")) {
                log.info("Negación de noticias: excluyendo todas las noticias");
                return new ResultadoChatbot("CONSULTAR_NOTICIAS", 
                    "No hay noticias disponibles (excluyendo: " + elementoNegado + ").");
            }
            
            noticias = noticias.stream()
                .filter(n -> {
                    String titulo = n.getTitulo() != null ? n.getTitulo().toLowerCase() : "";
                    String resumen = n.getResumen() != null ? n.getResumen().toLowerCase() : "";
                    return !titulo.contains(elementoLower) && !resumen.contains(elementoLower);
                })
                .collect(Collectors.toList());
                
            if (noticias.isEmpty()) {
                return new ResultadoChatbot("CONSULTAR_NOTICIAS", 
                    "No hay noticias disponibles (excluyendo: " + elementoNegado + ").");
            }
        }

        noticias = noticias.stream().limit(MAX_RESULTADOS).collect(Collectors.toList());

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_NOTICIAS", 
            noticias.size() == 1 ? "Hay una novedad reciente:" : "Hay " + noticias.size() + " novedades recientes:");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Noticia n : noticias) {
            Map<String, Object> item = new HashMap<>();
            item.put("titulo", n.getTitulo());
            item.put("resumen", n.getResumen());
            item.put("contenido", n.getContenido());
            if (n.getFechaPublicacion() != null) {
                item.put("fechaPublicacion", n.getFechaPublicacion().format(formatter));
            }
            resultado.getResultados().add(item);
        }

        resultado.setOpciones(Arrays.asList("Ver todas las noticias", "Ver noticias por categoría"));
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_NOTICIAS";
    }
}
