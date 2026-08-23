package com.darbot.chatbot.util;

import com.darbot.chatbot.entity.Correccion;
import com.darbot.chatbot.entity.Negacion;
import com.darbot.chatbot.entity.PatronNegacion;
import com.darbot.chatbot.entity.Sinonimo;
import com.darbot.chatbot.repository.CorreccionRepository;
import com.darbot.chatbot.repository.NegacionRepository;
import com.darbot.chatbot.repository.PatronNegacionRepository;
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
    private final NegacionRepository negacionRepository;
    private final PatronNegacionRepository patronNegacionRepository;

    private static final Pattern DIACRITICS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    /**
     * Normaliza un texto eliminando tildes, caracteres especiales,
     * corrigiendo typos y expandiendo sinónimos.
     */
    public String normalizar(String texto) {
        if (texto == null) return "";
        
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = DIACRITICS.matcher(normalizado).replaceAll("");
        normalizado = normalizado.replaceAll("[^a-zA-Z0-9\\s]", "");
        normalizado = normalizado.toLowerCase(Locale.ROOT).trim();

        normalizado = corregirTypos(normalizado);
        normalizado = expandirSinonimos(normalizado);

        log.debug("Texto normalizado: '{}' -> '{}'", texto, normalizado);
        return normalizado;
    }

    private String corregirTypos(String texto) {
        List<Correccion> correcciones = correccionRepository.findByActivaTrue();
        if (correcciones.isEmpty()) return texto;

        String resultado = texto;
        correcciones.sort((a, b) -> Integer.compare(b.getError().length(), a.getError().length()));

        for (Correccion c : correcciones) {
            String error = c.getError().toLowerCase();
            String correccion = c.getCorreccion().toLowerCase();
            resultado = resultado.replaceAll("\\b" + Pattern.quote(error) + "\\b", correccion);
        }

        return resultado;
    }

    private String expandirSinonimos(String texto) {
        List<Sinonimo> sinonimos = sinonimoRepository.findByActivaTrue();
        if (sinonimos.isEmpty()) return texto;

        Map<String, String> sinonimoToBase = new HashMap<>();
        for (Sinonimo s : sinonimos) {
            String base = s.getPalabraBase().toLowerCase();
            String sin = s.getSinonimo().toLowerCase();
            sinonimoToBase.putIfAbsent(sin, base);
        }

        String resultado = texto;
        for (Map.Entry<String, String> entry : sinonimoToBase.entrySet()) {
            String sinonimo = entry.getKey();
            String base = entry.getValue();
            resultado = resultado.replaceAll("\\b" + Pattern.quote(sinonimo) + "\\b", base);
        }

        return resultado;
    }

    public boolean contieneSinonimo(String texto, String palabraBase) {
        String textoNormalizado = normalizar(texto);
        String base = palabraBase.toLowerCase();
        
        if (textoNormalizado.contains(base)) return true;

        List<Sinonimo> sinonimos = sinonimoRepository.findByPalabraBase(base);
        for (Sinonimo s : sinonimos) {
            if (textoNormalizado.contains(s.getSinonimo().toLowerCase())) return true;
        }

        return false;
    }

    public Set<String> getPalabrasBase(String texto) {
        String textoNormalizado = normalizar(texto);
        Set<String> palabrasBase = new HashSet<>();
        
        for (String palabra : textoNormalizado.split("\\s+")) {
            List<Sinonimo> sinonimos = sinonimoRepository.findBySinonimo(palabra);
            for (Sinonimo s : sinonimos) {
                palabrasBase.add(s.getPalabraBase().toLowerCase());
            }
            palabrasBase.add(palabra);
        }
        
        return palabrasBase;
    }

    // ==================== NEGACIÓN AVANZADA ====================

    /**
     * Detecta si el texto contiene una negación.
     */
    public boolean contieneNegacion(String texto) {
        if (texto == null) return false;
        String textoLower = texto.toLowerCase();
        
        List<Negacion> negaciones = negacionRepository.findByActivaTrue();
        for (Negacion neg : negaciones) {
            if (textoLower.contains(neg.getPalabra())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detecta el tipo de negación en el texto.
     * Retorna: DIRECTA, IMPLICITA, EXCEPCION, o null si no hay negación.
     */
    public String detectarTipoNegacion(String texto) {
        if (texto == null) return null;
        String textoLower = texto.toLowerCase();
        
        List<Negacion> negaciones = negacionRepository.findByActivaTrue();
        for (Negacion neg : negaciones) {
            if (textoLower.contains(neg.getPalabra())) {
                return neg.getTipo();
            }
        }
        return null;
    }

    /**
     * Extrae la palabra o frase que está siendo negada.
     * Ej: "no quiero eventos" → "eventos"
     *     "eventos excepto fútbol" → "fútbol"
     *     "sin noticias" → "noticias"
     * 
     * Utiliza la tabla patrones_negacion de la base de datos.
     */
    public String extraerElementoNegado(String texto) {
        if (texto == null) return null;
        String textoLower = texto.toLowerCase();
        
        // Lista de stop words para ignorar
        List<String> stopWords = Arrays.asList(
            "que", "de", "el", "la", "los", "las", "un", "una", 
            "y", "o", "u", "en", "con", "por", "para", "sobre",
            "a", "ante", "bajo", "cabe", "contra", "desde", "durante",
            "entre", "hacia", "hasta", "mediante", "según", "sin", "sobre", "tras"
        );

        // 1. Obtener patrones desde la base de datos
        List<PatronNegacion> patrones = patronNegacionRepository.findByActivaTrueOrderByPrioridadDesc();
        
        for (PatronNegacion patron : patrones) {
            String patronStr = patron.getPatron().toLowerCase();
            if (textoLower.contains(patronStr)) {
                int index = textoLower.indexOf(patronStr) + patronStr.length();
                String resto = textoLower.substring(index).trim();
                String[] palabras = resto.split("\\s+");
                StringBuilder resultado = new StringBuilder();
                
                for (int i = 0; i < Math.min(3, palabras.length); i++) {
                    String palabra = palabras[i];
                    if (!stopWords.contains(palabra) && palabra.length() > 2) {
                        if (resultado.length() > 0) resultado.append(" ");
                        resultado.append(palabra);
                    }
                }
                if (resultado.length() > 0) {
                    log.debug("Elemento negado extraído: '{}' desde patrón '{}'", resultado, patronStr);
                    return resultado.toString();
                }
            }
        }

        // 2. Fallback: Verificar palabras de negación individuales
        List<Negacion> negaciones = negacionRepository.findByActivaTrue();
        for (Negacion neg : negaciones) {
            String palabraNeg = neg.getPalabra().toLowerCase();
            if (textoLower.contains(palabraNeg)) {
                int index = textoLower.indexOf(palabraNeg) + palabraNeg.length();
                String resto = textoLower.substring(index).trim();
                
                // Si es EXCEPCION, buscar después de la palabra clave
                if (neg.getTipo().equals("EXCEPCION")) {
                    return extraerPalabrasDespuesDe(resto, Arrays.asList("excepto", "menos", "salvo"), stopWords);
                }
                
                // Para negación directa, tomar la siguiente palabra significativa
                String[] palabras = resto.split("\\s+");
                for (String palabra : palabras) {
                    if (!stopWords.contains(palabra) && palabra.length() > 2) {
                        return palabra;
                    }
                }
            }
        }
        
        return null;
    }

    private String extraerPalabrasDespuesDe(String texto, List<String> marcadores, List<String> stopWords) {
        if (texto == null || texto.isEmpty()) return "";
        
        String[] palabras = texto.split("\\s+");
        StringBuilder resultado = new StringBuilder();
        
        for (String palabra : palabras) {
            if (marcadores.stream().anyMatch(m -> palabra.contains(m))) continue;
            if (stopWords.contains(palabra)) continue;
            if (resultado.length() > 0) resultado.append(" ");
            resultado.append(palabra);
        }
        
        return resultado.toString();
    }

    /**
     * Elimina negaciones del texto para análisis de intención.
     */
    public String eliminarNegaciones(String texto) {
        if (texto == null) return "";
        
        List<Negacion> negaciones = negacionRepository.findByActivaTrue();
        String resultado = texto;
        for (Negacion neg : negaciones) {
            resultado = resultado.replaceAll("\\b" + Pattern.quote(neg.getPalabra()) + "\\b", "");
        }
        return resultado.trim();
    }

    /**
     * Detecta si una pregunta es compuesta (contiene múltiples intenciones).
     */
    public boolean esPreguntaCompuesta(String texto) {
        if (texto == null || texto.trim().isEmpty()) return false;
        
        String textoLower = texto.toLowerCase();
        String[] conectores = {" y ", " además ", " también ", " & ", ",", " más "};
        
        int count = 0;
        for (String conector : conectores) {
            if (textoLower.contains(conector)) count++;
        }
        
        if (count >= 2) return true;
        
        String[] palabrasClave = {"evento", "noticia", "sede", "contacto", "horario", "servicio"};
        int intencionesDetectadas = 0;
        for (String palabra : palabrasClave) {
            if (textoLower.contains(palabra)) intencionesDetectadas++;
        }
        
        return intencionesDetectadas >= 2;
    }

    public List<String> dividirPreguntaCompuesta(String texto) {
        String[] partes = texto.split("( y | además | también | & | , | más )");
        List<String> resultado = new ArrayList<>();
        
        for (String parte : partes) {
            String trimmed = parte.trim();
            trimmed = trimmed.replaceAll("^(qué|cuál|cuáles|dónde|cuándo|quién|qué|para|con|sin)\\s+", "");
            trimmed = trimmed.replaceAll("\\s+(hay|tiene|tienen|está|están)$", "");
            if (trimmed.length() >= 3 && !trimmed.isEmpty()) {
                resultado.add(trimmed);
            }
        }
        
        if (resultado.isEmpty() && texto.trim().length() > 3) {
            resultado.add(texto.trim());
        }
        
        return resultado;
    }

    public boolean esNegacionDirecta(String texto) {
        if (texto == null) return false;
        String textoLower = texto.toLowerCase();
        
        // Usar patrones desde la BD
        List<PatronNegacion> patrones = patronNegacionRepository.findByActivaTrueOrderByPrioridadDesc();
        for (PatronNegacion patron : patrones) {
            if (patron.getTipo().equals("DIRECTA") && textoLower.contains(patron.getPatron().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}