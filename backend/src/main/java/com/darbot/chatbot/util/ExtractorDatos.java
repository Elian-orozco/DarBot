package com.darbot.chatbot.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExtractorDatos {

    private static final Pattern PATRON_FECHA = Pattern.compile(
        "(\\d{1,2}\\s+de\\s+[a-z]+\\s+del?\\s+\\d{4})|" +
        "(\\d{1,2}/\\d{1,2}/\\d{4})|" +
        "(\\d{4}-\\d{1,2}-\\d{1,2})|" +
        "(hoy|mañana|pasado mañana|ayer)"
    );

    private static final Pattern PATRON_GRADO = Pattern.compile(
        "(preescolar|primero|segundo|tercero|cuarto|quinto|sexto|séptimo|octavo|noveno|décimo|undécimo|once?|" +
        "transición|párvulos|maternal|jardín|prekínder|kínder|" +
        "\\d+\\s*(?:°|º|grado|grados|mo|° grado))"
    );

    private static final Pattern PATRON_HORA = Pattern.compile(
        "(\\d{1,2}:\\d{2}\\s*(?:am|pm)?)|" +
        "(\\d{1,2}\\s*(?:am|pm|de la mañana|de la tarde|de la noche))"
    );

    private static final Pattern PATRON_NUMERO = Pattern.compile("\\d+");

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
            String grado = gradoMatcher.group();
            entidades.put("grado", grado);
            // Normalizar grado a número
            entidades.put("grado_numero", normalizarGrado(grado));
        }

        // Extraer horas
        Matcher horaMatcher = PATRON_HORA.matcher(textoLower);
        if (horaMatcher.find()) {
            entidades.put("hora", horaMatcher.group());
        }

        // Extraer números (para IDs, cantidades)
        Matcher numeroMatcher = PATRON_NUMERO.matcher(textoLower);
        List<String> numeros = new ArrayList<>();
        while (numeroMatcher.find()) {
            numeros.add(numeroMatcher.group());
        }
        if (!numeros.isEmpty()) {
            entidades.put("numeros", numeros);
        }

        // Detectar tipo de evento
        if (textoLower.contains("reunión") || textoLower.contains("reunion")) {
            entidades.put("tipo_evento", "REUNION");
        } else if (textoLower.contains("taller")) {
            entidades.put("tipo_evento", "TALLER");
        } else if (textoLower.contains("feria")) {
            entidades.put("tipo_evento", "FERIA");
        } else if (textoLower.contains("conferencia")) {
            entidades.put("tipo_evento", "CONFERENCIA");
        } else if (textoLower.contains("deporte") || textoLower.contains("deportivo")) {
            entidades.put("tipo_evento", "DEPORTIVO");
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
        } else if (textoLower.contains("informe")) {
            entidades.put("tipo_documento", "INFORME");
        }

        // Detectar tipo de contacto
        if (textoLower.contains("teléfono") || textoLower.contains("telefono") || textoLower.contains("celular")) {
            entidades.put("tipo_contacto", "TELEFONO");
        } else if (textoLower.contains("correo") || textoLower.contains("email")) {
            entidades.put("tipo_contacto", "CORREO");
        } else if (textoLower.contains("dirección") || textoLower.contains("direccion") || textoLower.contains("ubicación")) {
            entidades.put("tipo_contacto", "DIRECCION");
        }

        return entidades;
    }

    private Integer normalizarGrado(String grado) {
        Map<String, Integer> grados = new HashMap<>();
        grados.put("preescolar", 0);
        grados.put("transición", 0);
        grados.put("primero", 1);
        grados.put("segundo", 2);
        grados.put("tercero", 3);
        grados.put("cuarto", 4);
        grados.put("quinto", 5);
        grados.put("sexto", 6);
        grados.put("séptimo", 7);
        grados.put("octavo", 8);
        grados.put("noveno", 9);
        grados.put("décimo", 10);
        grados.put("undécimo", 11);
        grados.put("once", 11);

        String gradoLower = grado.toLowerCase().trim();
        for (Map.Entry<String, Integer> entry : grados.entrySet()) {
            if (gradoLower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Intentar extraer número
        try {
            return Integer.parseInt(gradoLower.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public LocalDate parsearFecha(String fechaStr) {
        if (fechaStr == null) return null;

        // Manejar fechas relativas
        if (fechaStr.contains("hoy")) return LocalDate.now();
        if (fechaStr.contains("mañana")) return LocalDate.now().plusDays(1);
        if (fechaStr.contains("pasado mañana")) return LocalDate.now().plusDays(2);
        if (fechaStr.contains("ayer")) return LocalDate.now().minusDays(1);

        // Intentar parsear fechas en formato dd/MM/yyyy
        try {
            return LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {}

        // Intentar parsear fechas en formato yyyy-MM-dd
        try {
            return LocalDate.parse(fechaStr);
        } catch (DateTimeParseException e) {}

        // Intentar parsear fechas en formato "dd de mes de yyyy"
        try {
            String[] partes = fechaStr.split("\\s+");
            if (partes.length >= 5) {
                int dia = Integer.parseInt(partes[0]);
                String mesStr = partes[2];
                int año = Integer.parseInt(partes[4]);
                Map<String, Integer> meses = new HashMap<>();
                meses.put("enero", 1); meses.put("febrero", 2); meses.put("marzo", 3);
                meses.put("abril", 4); meses.put("mayo", 5); meses.put("junio", 6);
                meses.put("julio", 7); meses.put("agosto", 8); meses.put("septiembre", 9);
                meses.put("octubre", 10); meses.put("noviembre", 11); meses.put("diciembre", 12);
                
                Integer mes = meses.get(mesStr);
                if (mes != null) {
                    return LocalDate.of(año, mes, dia);
                }
            }
        } catch (Exception e) {}

        return null;
    }
}
