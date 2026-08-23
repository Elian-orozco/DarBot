package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentoHandler implements IntencionHandler {

    private final DocumentoRepository documentoRepository;
    private static final int MAX_RESULTADOS = 5;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("DocumentoHandler - elementoNegado: '{}'", elementoNegado);
        
        List<Documento> documentos = documentoRepository.findByEstado("ACTIVO");
        
        if (documentos == null || documentos.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_DOCUMENTOS", 
                "No hay documentos activos disponibles en este momento.");
        }

        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            
            if (elementoLower.contains("documento") || elementoLower.equals("documentos") || 
                elementoLower.contains("manual") || elementoLower.contains("archivo")) {
                return new ResultadoChatbot("CONSULTAR_DOCUMENTOS", 
                    "No hay documentos disponibles (excluyendo: " + elementoNegado + ").");
            }
            
            documentos = documentos.stream()
                .filter(d -> {
                    String titulo = d.getTitulo() != null ? d.getTitulo().toLowerCase() : "";
                    String descripcion = d.getDescripcion() != null ? d.getDescripcion().toLowerCase() : "";
                    return !titulo.contains(elementoLower) && !descripcion.contains(elementoLower);
                })
                .collect(Collectors.toList());
                
            if (documentos.isEmpty()) {
                return new ResultadoChatbot("CONSULTAR_DOCUMENTOS", 
                    "No hay documentos disponibles (excluyendo: " + elementoNegado + ").");
            }
        }

        if (entidades.containsKey("tipo_documento")) {
            String tipo = (String) entidades.get("tipo_documento");
            documentos = documentos.stream()
                .filter(d -> d.getTipo() != null && d.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
        }

        if (documentos.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_DOCUMENTOS", 
                "No encontré documentos que coincidan con tu búsqueda.");
        }

        documentos = documentos.stream().limit(MAX_RESULTADOS).collect(Collectors.toList());

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_DOCUMENTOS", 
            "Documentos disponibles:");

        for (Documento d : documentos) {
            Map<String, Object> item = new HashMap<>();
            item.put("titulo", d.getTitulo());
            item.put("descripcion", d.getDescripcion());
            item.put("tipo", d.getTipo());
            item.put("nombreArchivo", d.getNombreArchivo());
            item.put("rutaArchivo", d.getRutaArchivo());
            resultado.getResultados().add(item);
        }

        resultado.setOpciones(Arrays.asList("Ver todos los documentos", "Buscar documentos"));
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_DOCUMENTOS";
    }
}
