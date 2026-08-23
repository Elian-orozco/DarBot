package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;

import java.util.Map;

public interface IntencionHandler {
    ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado);
    String getIntencion();
}
