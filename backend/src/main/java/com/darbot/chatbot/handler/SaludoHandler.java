package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.chatbot.repository.MensajeSistemaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaludoHandler implements IntencionHandler {

    private final MensajeSistemaRepository mensajeSistemaRepository;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("SaludoHandler - elementoNegado: '{}'", elementoNegado);
        
        String saludo = obtenerSaludoSegunHora();
        String bienvenida = obtenerMensaje("saludo_bienvenida");
        String presentacion = obtenerMensaje("saludo_presentacion");
        String opciones = obtenerMensaje("saludo_opciones");
        String preguntaFinal = obtenerMensaje("saludo_pregunta_final");
        
        String mensaje = saludo + " " + bienvenida + "\n\n" +
                         presentacion + "\n" +
                         opciones + "\n\n" +
                         preguntaFinal;

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_SALUDO", mensaje);
        resultado.setOpciones(Arrays.asList(
            "¿Qué eventos hay?",
            "¿Cuál es el horario de atención?",
            "¿Dónde están las sedes?"
        ));
        
        return resultado;
    }

    private String obtenerSaludoSegunHora() {
        LocalTime ahora = LocalTime.now();
        String clave;
        
        if (ahora.isBefore(LocalTime.NOON)) {
            clave = "saludo_buenos_dias";
        } else if (ahora.isBefore(LocalTime.of(18, 0))) {
            clave = "saludo_buenas_tardes";
        } else {
            clave = "saludo_buenas_noches";
        }
        
        return obtenerMensaje(clave);
    }

    private String obtenerMensaje(String clave) {
        return mensajeSistemaRepository.findByClave(clave)
                .map(m -> m.getMensaje())
                .orElse("¡Hola! Soy DarBot, tu asistente virtual. 🤖");
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_SALUDO";
    }
}
