package com.darbot.chatbot.handler;

import com.darbot.chatbot.dto.ResultadoChatbot;
import com.darbot.institucional.entity.InformacionInstitucional;
import com.darbot.institucional.repository.InformacionInstitucionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstitucionHandler implements IntencionHandler {

    private final InformacionInstitucionalRepository infoRepository;

    @Override
    public ResultadoChatbot procesar(String texto, Map<String, Object> entidades, String elementoNegado) {
        log.info("InstitucionHandler - elementoNegado: '{}'", elementoNegado);
        
        InformacionInstitucional info = infoRepository.findAll().stream().findFirst().orElse(null);
        
        if (info == null) {
            return new ResultadoChatbot("CONSULTAR_INSTITUCION", 
                "Información institucional no disponible.");
        }

        // Construir mensaje
        StringBuilder mensaje = new StringBuilder();
        if (info.getNombreInstitucion() != null) {
            mensaje.append("🏫 **").append(info.getNombreInstitucion()).append("**\n\n");
        }
        if (info.getDescripcion() != null) {
            mensaje.append(info.getDescripcion()).append("\n\n");
        }
        if (info.getMision() != null) {
            mensaje.append("**Misión:** ").append(info.getMision()).append("\n\n");
        }
        if (info.getVision() != null) {
            mensaje.append("**Visión:** ").append(info.getVision()).append("\n\n");
        }
        if (info.getValores() != null) {
            mensaje.append("**Valores:** ").append(info.getValores()).append("\n\n");
        }
        
        mensaje.append("📞 **Contacto general:** ").append(info.getTelefonoGeneral() != null ? info.getTelefonoGeneral() : "No disponible").append("\n");
        mensaje.append("✉️ **Correo:** ").append(info.getCorreoGeneral() != null ? info.getCorreoGeneral() : "No disponible").append("\n");
        mensaje.append("🌐 **Web:** ").append(info.getSitioWeb() != null ? info.getSitioWeb() : "No disponible").append("\n\n");
        mensaje.append("¿Hay algo específico que quieras saber sobre la institución?");

        ResultadoChatbot resultado = new ResultadoChatbot("CONSULTAR_INSTITUCION", mensaje.toString());
        
        Map<String, Object> item = new HashMap<>();
        item.put("nombre", info.getNombreInstitucion());
        item.put("descripcion", info.getDescripcion());
        item.put("mision", info.getMision());
        item.put("vision", info.getVision());
        item.put("valores", info.getValores());
        item.put("telefono", info.getTelefonoGeneral());
        item.put("correo", info.getCorreoGeneral());
        item.put("web", info.getSitioWeb());
        resultado.getResultados().add(item);
        
        return resultado;
    }

    @Override
    public String getIntencion() {
        return "CONSULTAR_INSTITUCION";
    }
}
