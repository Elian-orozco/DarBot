package com.darbot.chatbot.util;

import com.darbot.chatbot.entity.Correccion;
import com.darbot.chatbot.entity.Sinonimo;
import com.darbot.chatbot.repository.CorreccionRepository;
import com.darbot.chatbot.repository.SinonimoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class LenguajeUtil {

    private final CorreccionRepository correccionRepository;
    private final SinonimoRepository sinonimoRepository;

    private static final Pattern DIACRITICS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    /**
     * Normaliza un texto eliminando tildes, caracteres especiales,
     * corrigiendo typos y expandiendo sinónimos.
     */
    public String normalizar(String texto) {
        if (texto == null) return "";
        
        // 1. Normalizar tildes y caracteres especiales
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = DIACRITICS.matcher(normalizado).replaceAll("");
        normalizado = normalizado.replaceAll("[^a-zA-Z0-9\\s]", "");
        normalizado = normalizado.toLowerCase(Locale.ROOT).trim();

        // 2. Corregir typos
        normalizado = corregirTypos(normalizado);

        // 3. Expandir sinónimos
        normalizado = expandirSinonimos(normalizado);

        log.debug("Texto normalizado: '{}' -> '{}'", texto, normalizado);
        return normalizado;
    }

    /**
     * Corrige errores de escritura comunes usando la tabla correcciones.
     */
    private String corregirTypos(String texto) {
        List<Correccion> correcciones = correccionRepository.findByActivaTrue();
        if (correcciones.isEmpty()) {
            return texto;
        }

        String resultado = texto;
        
        // Ordenar por longitud de error (más largo primero para evitar coincidencias parciales)
        correcciones.sort((a, b) -> Integer.compare(b.getError().length(), a.getError().length()));

        for (Correccion c : correcciones) {
            String error = c.getError().toLowerCase();
            String correccion = c.getCorreccion().toLowerCase();
            
            // Reemplazar palabra completa, no subcadenas
            resultado = resultado.replaceAll("\\b" + Pattern.quote(error) + "\\b", correccion);
        }

        return resultado;
    }

    /**
     * Expande sinónimos en el texto para mejorar la detección de intenciones.
     * Versión mejorada que reemplaza directamente los sinónimos por sus bases.
     */
    private String expandirSinonimos(String texto) {
        List<Sinonimo> sinonimos = sinonimoRepository.findByActivaTrue();
        if (sinonimos.isEmpty()) {
            return texto;
        }

        // Construir mapa: sinónimo -> palabra_base (con prioridad)
        Map<String, String> sinonimoToBase = new HashMap<>();
        for (Sinonimo s : sinonimos) {
            String base = s.getPalabraBase().toLowerCase();
            String sin = s.getSinonimo().toLowerCase();
            // Si ya existe, mantener el que tiene más peso (usamos el primero)
            sinonimoToBase.putIfAbsent(sin, base);
        }

        // Reemplazar sinónimos en el texto (palabras completas)
        String resultado = texto;
        for (Map.Entry<String, String> entry : sinonimoToBase.entrySet()) {
            String sinonimo = entry.getKey();
            String base = entry.getValue();
            // Reemplazar palabra completa
            resultado = resultado.replaceAll("\\b" + Pattern.quote(sinonimo) + "\\b", base);
        }

        // Verificar si el resultado es diferente (para logging)
        if (!resultado.equals(texto)) {
            log.debug("Sinónimos expandidos: '{}' -> '{}'", texto, resultado);
        }

        return resultado;
    }

    /**
     * Verifica si el texto contiene sinónimos de una palabra base.
     */
    public boolean contieneSinonimo(String texto, String palabraBase) {
        String textoNormalizado = normalizar(texto);
        String base = palabraBase.toLowerCase();
        
        // Verificar directo
        if (textoNormalizado.contains(base)) {
            return true;
        }

        // Verificar sinónimos
        List<Sinonimo> sinonimos = sinonimoRepository.findByPalabraBase(base);
        for (Sinonimo s : sinonimos) {
            if (textoNormalizado.contains(s.getSinonimo().toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtiene todas las palabras base de un texto (con sinónimos expandidos).
     */
    public Set<String> getPalabrasBase(String texto) {
        String textoNormalizado = normalizar(texto);
        Set<String> palabrasBase = new HashSet<>();
        
        for (String palabra : textoNormalizado.split("\\s+")) {
            // Buscar si la palabra es un sinónimo
            List<Sinonimo> sinonimos = sinonimoRepository.findBySinonimo(palabra);
            for (Sinonimo s : sinonimos) {
                palabrasBase.add(s.getPalabraBase().toLowerCase());
            }
            // Agregar la palabra original
            palabrasBase.add(palabra);
        }
        
        return palabrasBase;
    }

    /**
     * Detecta si el texto contiene una negación.
     */
    public boolean contieneNegacion(String texto) {
        if (texto == null) return false;
        String textoLower = texto.toLowerCase();
        String[] negaciones = {"no", "nunca", "jamás", "ni", "sin", "ningún", "ninguna", 
                               "tampoco", "ni siquiera", "nada", "nadie"};
        for (String neg : negaciones) {
            if (textoLower.contains(neg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina negaciones del texto para análisis de intención.
     */
    public String eliminarNegaciones(String texto) {
        if (texto == null) return "";
        String[] negaciones = {"no", "nunca", "jamás", "ni", "sin", "ningún", "ninguna", 
                               "tampoco", "ni siquiera", "nada", "nadie"};
        String resultado = texto;
        for (String neg : negaciones) {
            resultado = resultado.replaceAll("\\b" + Pattern.quote(neg) + "\\b", "");
        }
        return resultado.trim();
    }

    /**
     * Detecta si una pregunta es compuesta (contiene múltiples intenciones).
     */
    public boolean esPreguntaCompuesta(String texto) {
        if (texto == null || texto.trim().isEmpty()) return false;
        
        String textoLower = texto.toLowerCase();
        // Conectores que indican múltiples intenciones
        String[] conectores = {" y ", " además ", " también ", " & ", ",", " más "};
        
        // Contar cuántos conectores hay
        int count = 0;
        for (String conector : conectores) {
            if (textoLower.contains(conector)) {
                count++;
            }
        }
        
        // Si hay al menos 2 conectores, es compuesta
        // O si hay 1 conector y el texto tiene varias partes
        if (count >= 2) {
            return true;
        }
        
        // Verificar si el texto tiene varias palabras clave de intenciones
        String[] palabrasClave = {"evento", "noticia", "sede", "contacto", "horario", "servicio"};
        int intencionesDetectadas = 0;
        for (String palabra : palabrasClave) {
            if (textoLower.contains(palabra)) {
                intencionesDetectadas++;
            }
        }
        
        return intencionesDetectadas >= 2;
    }

    /**
     * Divide una pregunta compuesta en partes individuales.
     */
    public List<String> dividirPreguntaCompuesta(String texto) {
        // Dividir por conectores comunes
        String[] partes = texto.split("( y | además | también | & | , | más )");
        List<String> resultado = new ArrayList<>();
        
        for (String parte : partes) {
            String trimmed = parte.trim();
            // Limpiar palabras vacías comunes al inicio
            trimmed = trimmed.replaceAll("^(qué|cuál|cuáles|dónde|cuándo|quién|qué|para|con|sin)\\s+", "");
            // Limpiar palabras vacías al final
            trimmed = trimmed.replaceAll("\\s+(hay|tiene|tienen|está|están)$", "");
            if (trimmed.length() >= 3 && !trimmed.isEmpty()) {
                resultado.add(trimmed);
            }
        }
        
        // Si no se dividió correctamente, intentar con el texto original
        if (resultado.isEmpty() && texto.trim().length() > 3) {
            resultado.add(texto.trim());
        }
        
        return resultado;
    }

    /**
     * Agrega un sinónimo a la base de datos (útil para administración).
     */
    @jakarta.transaction.Transactional
    public void agregarSinonimo(String palabraBase, String sinonimo, String nombreIntencion) {
        // Buscar la intención
        var intencionOpt = sinonimoRepository.findByPalabraBase(palabraBase);
        // ... implementar si es necesario
    }
}