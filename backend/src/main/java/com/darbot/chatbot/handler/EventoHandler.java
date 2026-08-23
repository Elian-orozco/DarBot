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
    private static final int MAX_RESULTADOS = 10;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("EventoHandler - elementoNegado: '{}'", elementoNegado);

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = null;

        // Verificar si hay rango de fechas
        if (entidades.containsKey("fecha_desde") && entidades.containsKey("fecha_hasta")) {
            fechaInicio = (LocalDate) entidades.get("fecha_desde");
            fechaFin = (LocalDate) entidades.get("fecha_hasta");
            log.info("Rango de fechas específico: {} - {}", fechaInicio, fechaFin);
        } else if (entidades.containsKey("rango_nombre")) {
            // Usar rango por nombre (hoy, esta semana, etc.)
            String rangoNombre = (String) entidades.get("rango_nombre");
            if (entidades.containsKey("fecha_desde")) {
                fechaInicio = (LocalDate) entidades.get("fecha_desde");
            }
            if (entidades.containsKey("fecha_hasta")) {
                fechaFin = (LocalDate) entidades.get("fecha_hasta");
            }
            log.info("Rango por nombre: {} -> {} - {}", rangoNombre, fechaInicio, fechaFin);
        }

        // Obtener eventos
        List<Evento> eventos;
        if (fechaFin != null) {
            eventos = eventoRepository.findByFechaBetweenOrderByFechaAsc(fechaInicio, fechaFin);
        } else {
            eventos = eventoRepository.findByFechaGreaterThanEqualOrderByFechaAsc(fechaInicio);
        }

        if (eventos == null || eventos.isEmpty()) {
            String mensaje = fechaFin != null ? 
                "No hay eventos programados entre el " + fechaInicio + " y el " + fechaFin + "." :
                "Actualmente no hay eventos programados próximos en la institución.";
            return new ResultadoChatbot("CONSULTAR_EVENTOS", mensaje);
        }

        // Aplicar filtro de negación
        if (elementoNegado != null && !elementoNegado.isEmpty()) {
            String elementoLower = elementoNegado.toLowerCase();
            
            if (elementoLower.contains("evento") || elementoLower.equals("eventos") || 
                elementoLower.equals("evento") || elementoLower.contains("actividad")) {
                return new ResultadoChatbot("CONSULTAR_EVENTOS", 
                    "No encontré eventos (excluyendo: " + elementoNegado + ").");
            }
            
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

        String mensaje;
        if (fechaFin != null) {
            mensaje = "Encontré " + eventos.size() + " eventos entre el " + fechaInicio + " y el " + fechaFin + ":";
        } else {
            mensaje = "Encontré " + eventos.size() + " eventos próximos:";
        }

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_EVENTOS", mensaje);

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
