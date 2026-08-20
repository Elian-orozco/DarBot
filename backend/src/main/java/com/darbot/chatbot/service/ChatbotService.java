package com.darbot.chatbot.service;

import com.darbot.chatbot.entity.*;
import com.darbot.chatbot.repository.*;
import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.repository.DocumentoRepository;
import com.darbot.contenidos.repository.EventoRepository;
import com.darbot.contenidos.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final FaqRepository faqRepository;
    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final PreguntaSinRespuestaRepository sinRespuestaRepository;
    private final EventoRepository eventoRepository;
    private final NoticiaRepository noticiaRepository;
    private final DocumentoRepository documentoRepository;

    public String procesarMensaje(String sessionId, String textoUsuario) {
        if (textoUsuario == null || textoUsuario.isBlank()) {
            return "Escribe tu consulta para que pueda ayudarte.";
        }

        Conversacion conversacion = conversacionRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Conversacion nuevaConv = new Conversacion();
                    nuevaConv.setSessionId(sessionId);
                    return conversacionRepository.save(nuevaConv);
                });

        Mensaje mensajeUser = new Mensaje();
        mensajeUser.setConversacion(conversacion);
        mensajeUser.setTipo("USER");
        mensajeUser.setContenido(textoUsuario);
        mensajeRepository.save(mensajeUser);

        String textoNormalizado = normalizarTexto(textoUsuario);
        String intencionDetectada = detectarIntencion(textoNormalizado);
        String respuestaBot;

        Faq faqEncontrada = buscarFaqMasRelacionada(textoNormalizado);
        if (faqEncontrada != null) {
            respuestaBot = faqEncontrada.getRespuesta();
            intencionDetectada = "CONSULTAR_FAQ";
        } else if ("CONSULTAR_EVENTOS".equals(intencionDetectada)) {
            respuestaBot = responderEventos();
        } else if ("CONSULTAR_NOTICIAS".equals(intencionDetectada)) {
            respuestaBot = responderNoticias();
        } else if ("CONSULTAR_DOCUMENTOS".equals(intencionDetectada)) {
            respuestaBot = responderDocumentos();
        } else if ("CONSULTAR_CONTACTO".equals(intencionDetectada)) {
            respuestaBot = "Puedes comunicarte con la institución por correo institucional o a través de coordinación. Si lo deseas, también puedes consultar la sección de contacto de la web.";
        } else if ("CONSULTAR_HORARIOS".equals(intencionDetectada)) {
            respuestaBot = "Los horarios de atención de la institución son los publicados en la web institucional. Si quieres, también puedo ayudarte con información de sedes o servicios.";
        } else {
            PreguntaSinRespuesta psr = new PreguntaSinRespuesta();
            psr.setPregunta(textoUsuario);
            psr.setIntentoIntencion(intencionDetectada);
            sinRespuestaRepository.save(psr);

            respuestaBot = "Lo siento, aún no tengo una respuesta para eso. He registrado tu consulta para que los administradores puedan mejorar mi base de conocimiento.";
        }

        Mensaje mensajeBot = new Mensaje();
        mensajeBot.setConversacion(conversacion);
        mensajeBot.setTipo("BOT");
        mensajeBot.setContenido(respuestaBot);
        mensajeBot.setIntencionDetectada(intencionDetectada);
        mensajeRepository.save(mensajeBot);

        return respuestaBot;
    }

    private String normalizarTexto(String texto) {
        String textoSinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return textoSinAcentos.toLowerCase(Locale.ROOT).trim();
    }

    private String detectarIntencion(String texto) {
        if (contieneCualquier(texto, "evento", "actividad", "agenda", "calendario", "proximo", "proximos")) {
            return "CONSULTAR_EVENTOS";
        }
        if (contieneCualquier(texto, "noticia", "novedad", "publicacion", "actualidad", "informacion reciente")) {
            return "CONSULTAR_NOTICIAS";
        }
        if (contieneCualquier(texto, "documento", "manual", "circular", "formato", "archivo", "descargar")) {
            return "CONSULTAR_DOCUMENTOS";
        }
        if (contieneCualquier(texto, "contacto", "telefono", "correo", "llamar", "escribir", "comunicarse", "mensaje")) {
            return "CONSULTAR_CONTACTO";
        }
        if (contieneCualquier(texto, "horario", "hora", "abre", "cierra", "atencion", "atención")) {
            return "CONSULTAR_HORARIOS";
        }
        return "DESCONOCIDA";
    }

    private Faq buscarFaqMasRelacionada(String textoUsuario) {
        List<Faq> faqs = faqRepository.findByActivaTrue();
        if (faqs == null || faqs.isEmpty()) {
            return null;
        }

        return faqs.stream()
                .filter(faq -> faq != null && faq.getPregunta() != null)
                .map(faq -> new AbstractMap.SimpleEntry<>(faq, calcularCoincidencia(textoUsuario, faq.getPregunta())))
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private int calcularCoincidencia(String textoUsuario, String preguntaFaq) {
        String texto = normalizarTexto(textoUsuario);
        String pregunta = normalizarTexto(preguntaFaq);

        if (texto.equals(pregunta) || texto.contains(pregunta) || pregunta.contains(texto)) {
            return 100;
        }

        int score = 0;
        for (String palabra : listarPalabras(texto)) {
            if (pregunta.contains(palabra)) {
                score += 5;
            }
        }

        for (String clave : List.of("contacto", "telefono", "correo", "llamar", "escribir", "comunicarse", "horario", "atencion", "evento", "noticia", "documento", "inscripcion", "matricula")) {
            if (texto.contains(clave) && pregunta.contains(clave)) {
                score += 10;
            }
        }

        return score;
    }

    private boolean contieneCualquier(String texto, String... palabras) {
        for (String palabra : palabras) {
            if (texto.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    private List<String> listarPalabras(String texto) {
        return Arrays.stream(texto.split("\\s+"))
                .map(this::limpiarToken)
                .filter(token -> !token.isBlank())
                .toList();
    }

    private String limpiarToken(String token) {
        return token.replaceAll("[^a-z0-9]", "");
    }

    private String responderEventos() {
        List<Evento> eventos = eventoRepository.findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate.now());
        if (eventos == null || eventos.isEmpty()) {
            return "Actualmente no hay eventos programados próximos en la institución.";
        }

        StringBuilder respuesta = new StringBuilder("Tenemos estos eventos próximos: ");
        eventos.stream().limit(3).forEach(evento -> {
            respuesta.append("\n- ").append(evento.getTitulo()).append(" (")
                    .append(evento.getFecha()).append(")");
        });
        return respuesta.toString();
    }

    private String responderNoticias() {
        List<Noticia> noticias = noticiaRepository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADA");
        if (noticias == null || noticias.isEmpty()) {
            return "No hay novedades publicadas en este momento.";
        }

        StringBuilder respuesta = new StringBuilder("Estas son las novedades recientes: ");
        noticias.stream().limit(3).forEach(noticia -> {
            respuesta.append("\n- ").append(noticia.getTitulo());
        });
        return respuesta.toString();
    }

    private String responderDocumentos() {
        List<Documento> documentos = documentoRepository.findByEstado("ACTIVO");
        if (documentos == null || documentos.isEmpty()) {
            return "No hay documentos activos disponibles en este momento.";
        }

        StringBuilder respuesta = new StringBuilder("Estos son los documentos disponibles: ");
        documentos.stream().limit(3).forEach(documento -> {
            respuesta.append("\n- ").append(documento.getTitulo());
        });
        return respuesta.toString();
    }
}