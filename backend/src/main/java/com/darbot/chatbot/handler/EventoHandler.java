package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventoHandler implements IntencionHandler {

    private final EventoRepository eventoRepository;
    private static final int MAX_RESULTADOS = 5;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("EventoHandler - elementoNegado: '{}'", elementoNegado);

        LocalDate fechaInicio = LocalDate.now();
        List<Evento> eventos = eventoRepository.findByFechaGreaterThanEqualOrderByFechaAsc(fechaInicio);
        
        if (eventos == null || eventos.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_EVENTOS", 
                "Actualmente no hay eventos programados próximos en la institución.");
        }

        // Aplicar filtro de negación
        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            
            // Si la negación es sobre "eventos", excluir TODOS
            if (elementoLower.contains("evento") || elementoLower.equals("eventos") || 
                elementoLower.equals("evento") || elementoLower.contains("actividad")) {
                log.info("Negación de eventos: excluyendo todos los eventos");
                return new ResultadoChatbot("CONSULTAR_EVENTOS", 
                    "No encontré eventos (excluyendo: " + elementoNegado + ").");
            }
            
            // Filtrar por título o descripción
            eventos = eventos.stream()
                .filter(e -> {
                    String titulo = e.getTitulo() != null ? e.getTitulo().toLowerCase() : "";
                    String descripcion = e.getDescripcion() != null ? e.getDescripcion().toLowerCase() : "";
                    return !titulo.contains(elementoLower) && !descripcion.contains(elementoLower);
                })
                .collect(Collectors.toList());
                
            if (eventos.isEmpty()) {
                return new ResultadoChatbot("CONSULTAR_EVENTOS", 
                    "No encontré eventos (excluyendo: " + elementoNegado + ").");
            }
        }

        // Filtrar por tipo de evento
        if (entidades.containsKey("tipo_evento")) {
            String tipo = (String) entidades.get("tipo_evento");
            eventos = eventos.stream()
                .filter(e -> e.getTitulo() != null && 
                    e.getTitulo().toLowerCase().contains(tipo.toLowerCase()))
                .collect(Collectors.toList());
        }

        // Filtrar por grado
        if (entidades.containsKey("grado")) {
            String grado = (String) entidades.get("grado");
            eventos = eventos.stream()
                .filter(e -> e.getDescripcion() != null && 
                    e.getDescripcion().toLowerCase().contains(grado.toLowerCase()))
                .collect(Collectors.toList());
        }

        if (eventos.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_EVENTOS", 
                "No encontré eventos que coincidan con tu búsqueda.");
        }

        eventos = eventos.stream().limit(MAX_RESULTADOS).collect(Collectors.toList());

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_EVENTOS", 
            eventos.size() == 1 ? "Encontré un evento próximo:" : "Encontré " + eventos.size() + " eventos próximos:");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Evento e : eventos) {
            Map<String, Object> item = new HashMap<>();
            item.put("titulo", e.getTitulo());
            item.put("descripcion", e.getDescripcion());
            item.put("fecha", e.getFecha().format(formatter));
            if (e.getHoraInicio() != null) item.put("horaInicio", e.getHoraInicio().toString());
            if (e.getHoraFin() != null) item.put("horaFin", e.getHoraFin().toString());
            if (e.getLugar() != null) item.put("lugar", e.getLugar());
            resultado.getResultados().add(item);
        }

        resultado.setOpciones(Arrays.asList("Ver todos los eventos", "Ver eventos por mes", "Ver eventos por sede"));
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_EVENTOS";
    }
}
