package com.darbot.chatbot.util;

import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class NormalizadorTexto {

    private static final Pattern DIACRITICS_AND_FRIENDS = 
        Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    public String normalizar(String texto) {
        if (texto == null) return "";
        
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        textoNormalizado = DIACRITICS_AND_FRIENDS.matcher(textoNormalizado).replaceAll("");
        
        // Remover caracteres especiales pero mantener letras y números
        textoNormalizado = textoNormalizado.replaceAll("[^a-zA-Z0-9\\s]", "");
        
        return textoNormalizado.toLowerCase(Locale.ROOT).trim();
    }

    public String limpiarToken(String token) {
        return token.replaceAll("[^a-z0-9]", "").trim();
    }

    public String[] tokenizar(String texto) {
        String normalizado = normalizar(texto);
        return normalizado.split("\\s+");
    }
}