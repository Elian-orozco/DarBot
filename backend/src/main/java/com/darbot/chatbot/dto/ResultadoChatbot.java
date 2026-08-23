package com.darbot.chatbot.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ResultadoChatbot {
    private String intencion;
    private String mensaje;
    private List<Map<String, Object>> resultados = new ArrayList<>();
    private List<String> opciones = new ArrayList<>();
    private Map<String, Object> entidades;
    
    public ResultadoChatbot() {}
    
    public ResultadoChatbot(String intencion, String mensaje) {
        this.intencion = intencion;
        this.mensaje = mensaje;
    }
}
