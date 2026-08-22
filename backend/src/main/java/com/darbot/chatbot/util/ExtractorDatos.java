package com.darbot.chatbot.util;

import com.darbot.chatbot.entity.Intencion;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExtractorDatos {

    // Patrones para extraer entidades
    private static final Pattern PATRON_FECHA = Pattern.compile(
        "(\\d{1,2}\\s+de\\s+[a-z]+\\s+del?\\s+\\d{4})|" +
        "(\\d{1,2}/\\d{1,2}/\\d{4})|" +
        "(\\d{4}-\\d{1,2}-\\d{1,2})"
    );

    private static final Pattern PATRON_GRADO = Pattern.compile(
        "(preescolar|primero|segundo|tercero|cuarto|quinto|sexto|séptimo|octavo|noveno|décimo|undécimo|once?|"
        + "transición|párvulos|maternal|jardín|prekínder|kínder)"
    );

    private static final Pattern PATRON_HORA = Pattern.compile(
        "(\\d{1,2}:\\d{2}\\s*(?:am|pm)?)|" +
        "(\\d{1,2}\\s*(?:am|pm|de la mañana|de la tarde|de la noche))"
    );

    public Map<String, Object> extraerEntidades(String texto) {
        Map<String, Object> entidades = new HashMap<>();
        String textoLower = texto.toLowerCase();

        // Extraer fechas
        Matcher fechaMatcher = PATRON_FECHA.matcher(textoLower);
        if (fechaMatcher.find()) {
            entidades.put("fecha", fechaMatcher.group());
        }

        // Extraer grados
        Matcher gradoMatcher = PATRON_GRADO.matcher(textoLower);
        if (gradoMatcher.find()) {
            entidades.put("grado", gradoMatcher.group());
        }

        // Extraer horas
        Matcher horaMatcher = PATRON_HORA.matcher(textoLower);
        if (horaMatcher.find()) {
            entidades.put("hora", horaMatcher.group());
        }

        // Detectar tipos de evento
        if (textoLower.contains("reunión") || textoLower.contains("reunion")) {
            entidades.put("tipo_evento", "REUNION");
        } else if (textoLower.contains("taller")) {
            entidades.put("tipo_evento", "TALLER");
        } else if (textoLower.contains("feria")) {
            entidades.put("tipo_evento", "FERIA");
        } else if (textoLower.contains("conferencia")) {
            entidades.put("tipo_evento", "CONFERENCIA");
        }

        // Detectar tipo de documento
        if (textoLower.contains("manual")) {
            entidades.put("tipo_documento", "MANUAL");
        } else if (textoLower.contains("circular")) {
            entidades.put("tipo_documento", "CIRCULAR");
        } else if (textoLower.contains("formato")) {
            entidades.put("tipo_documento", "FORMATO");
        } else if (textoLower.contains("guía") || textoLower.contains("guia")) {
            entidades.put("tipo_documento", "GUIA");
        }

        // Detectar tipo de contacto
        if (textoLower.contains("teléfono") || textoLower.contains("telefono") || textoLower.contains("celular")) {
            entidades.put("tipo_contacto", "TELEFONO");
        } else if (textoLower.contains("correo") || textoLower.contains("email")) {
            entidades.put("tipo_contacto", "CORREO");
        } else if (textoLower.contains("dirección") || textoLower.contains("direccion")) {
            entidades.put("tipo_contacto", "DIRECCION");
        }

        return entidades;
    }

    public String extraerPalabrasClave(String texto, Intencion intencion) {
        // Extraer palabras que coinciden con las palabras clave de la intención
        String[] tokens = texto.toLowerCase().split("\\s+");
        StringBuilder builder = new StringBuilder();
        
        for (String token : tokens) {
            for (var pc : intencion.getPalabrasClave()) {
                if (token.contains(pc.getPalabra()) || pc.getPalabra().contains(token)) {
                    if (builder.length() > 0) builder.append(" ");
                    builder.append(token);
                    break;
                }
            }
        }
        
        return builder.toString();
    }
}