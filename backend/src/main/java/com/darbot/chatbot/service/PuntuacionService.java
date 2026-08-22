package com.darbot.chatbot.service;

import com.darbot.chatbot.entity.Faq;
import com.darbot.chatbot.repository.FaqRepository;
import com.darbot.chatbot.util.NormalizadorTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuntuacionService {

    private final FaqRepository faqRepository;
    private final NormalizadorTexto normalizador;

    public Map<Faq, Double> calcularPuntuacionFaq(String texto) {
        String textoNormalizado = normalizador.normalizar(texto);
        List<Faq> faqs = faqRepository.findByActivaTrue();
        
        Map<Faq, Double> puntuaciones = new HashMap<>();
        
        for (Faq faq : faqs) {
            double puntuacion = calcularCoincidenciaFaq(textoNormalizado, faq);
            if (puntuacion > 0) {
                puntuaciones.put(faq, puntuacion);
            }
        }
        
        return puntuaciones;
    }

    private double calcularCoincidenciaFaq(String texto, Faq faq) {
        String preguntaFaq = normalizador.normalizar(faq.getPregunta());
        String[] tokensTexto = texto.split("\\s+");
        String[] tokensFaq = preguntaFaq.split("\\s+");

        double coincidencia = 0.0;
        
        for (String token : tokensTexto) {
            for (String faqToken : tokensFaq) {
                if (token.contains(faqToken) || faqToken.contains(token)) {
                    coincidencia += 1.0 / tokensTexto.length;
                    break;
                }
            }
        }

        // Si la pregunta del FAQ está contenida exactamente en el texto
        if (texto.contains(preguntaFaq) || preguntaFaq.contains(texto)) {
            coincidencia = 1.0;
        }

        return Math.min(coincidencia, 1.0);
    }

    public Optional<Faq> obtenerMejorFaq(String texto) {
        Map<Faq, Double> puntuaciones = calcularPuntuacionFaq(texto);
        
        if (puntuaciones.isEmpty()) {
            return Optional.empty();
        }

        return puntuaciones.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .filter(entry -> entry.getValue() >= 0.4)
            .map(Map.Entry::getKey);
    }

    public <T> Map<T, Double> normalizarPuntuaciones(Map<T, Double> puntuaciones) {
        if (puntuaciones.isEmpty()) return new HashMap<>();
        
        double max = Collections.max(puntuaciones.values());
        if (max == 0) return puntuaciones;
        
        return puntuaciones.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue() / max
            ));
    }
}