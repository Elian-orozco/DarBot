package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.chatbot.entity.Faq;
import com.darbot.chatbot.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class HorarioHandler implements IntencionHandler {

    private final FaqRepository faqRepository;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("HorarioHandler - elementoNegado: '{}'", elementoNegado);
        
        List<Faq> horariosFaq = faqRepository.findByActivaTrue().stream()
            .filter(f -> f.getCategoria() != null && 
                (f.getCategoria().equalsIgnoreCase("HORARIOS") || 
                 f.getCategoria().equalsIgnoreCase("HORARIO")))
            .collect(Collectors.toList());

        if (horariosFaq.isEmpty()) {
            return new ResultadoChatbot("CONSULTAR_HORARIOS",
                "Los horarios de atención de la institución son los publicados en la web institucional.");
        }

        StringBuilder mensaje = new StringBuilder("🕐 **Horarios de atención:**\n\n");
        for (Faq f : horariosFaq) {
            mensaje.append("• ").append(f.getPregunta()).append("\n");
            mensaje.append("  ").append(f.getRespuesta()).append("\n\n");
        }

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_HORARIOS", mensaje.toString());
        
        for (Faq f : horariosFaq) {
            Map<String, Object> item = new HashMap<>();
            item.put("pregunta", f.getPregunta());
            item.put("respuesta", f.getRespuesta());
            resultado.getResultados().add(item);
        }
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_HORARIOS";
    }
}
