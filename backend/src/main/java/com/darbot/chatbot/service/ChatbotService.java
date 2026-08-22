package com.darbot.chatbot.service;
import com.darbot.chatbot.dto.ChatbotRespuesta;
import lombok.extern.slf4j.Slf4j;

import com.darbot.chatbot.entity.*;
import com.darbot.chatbot.repository.*;
import com.darbot.chatbot.util.NormalizadorTexto;
import com.darbot.chatbot.util.ExtractorDatos;
import com.darbot.common.exception.BadRequestException;
import com.darbot.common.exception.ChatbotException;
import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.repository.DocumentoRepository;
import com.darbot.contenidos.repository.EventoRepository;
import com.darbot.contenidos.repository.NoticiaRepository;
import com.darbot.institucional.entity.Contacto;
import com.darbot.institucional.entity.Sede;
import com.darbot.institucional.repository.ContactoRepository;
import com.darbot.institucional.repository.SedeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private final FaqRepository faqRepository;
    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final PreguntaSinRespuestaRepository sinRespuestaRepository;
    private final EventoRepository eventoRepository;
    private final NoticiaRepository noticiaRepository;
    private final DocumentoRepository documentoRepository;
    private final SedeRepository sedeRepository;
    private final ContactoRepository contactoRepository;

    private final IntencionService intencionService;
    private final ContextoService contextoService;
    private final PuntuacionService puntuacionService;
    private final NormalizadorTexto normalizador;
    private final ExtractorDatos extractorDatos;

    private static final int MAX_RESULTADOS = 5;

    public ChatbotRespuesta procesarMensaje(String sessionId, String textoUsuario) {
        validarEntrada(sessionId, textoUsuario);

        try {
            Conversacion conversacion = obtenerOCrearConversacion(sessionId);
            guardarMensaje(conversacion, "USER", textoUsuario);

            String textoNormalizado = normalizador.normalizar(textoUsuario);
            
            // 1. Verificar si es pregunta de contexto
            boolean esContexto = contextoService.esPreguntaDeContexto(textoNormalizado);
            Optional<ContextoConversacion> contextoOpt = contextoService.obtenerContexto(conversacion);
            
            String respuesta;
            String intencionDetectada = "DESCONOCIDA";
            Map<String, Object> entidadesExtraidas = new HashMap<>();

            if (esContexto && contextoOpt.isPresent()) {
                // Usar contexto para responder
                respuesta = responderConContexto(textoNormalizado, contextoOpt.get(), conversacion);
                intencionDetectada = "CONTEXTO_" + contextoOpt.get().getUltimaIntencion();
            } else {
    // 2. Primero detectar intención
    Optional<Intencion> intencionOpt = intencionService.detectarIntencion(textoNormalizado);
    
    if (intencionOpt.isPresent()) {
        Intencion intencion = intencionOpt.get();
        intencionDetectada = intencion.getNombre();
        
        // Extraer entidades de la pregunta
        entidadesExtraidas = extractorDatos.extraerEntidades(textoNormalizado);
        
        respuesta = procesarPorIntencion(intencion, textoNormalizado, entidadesExtraidas);
    } else {
        // 3. Si no hay intención, buscar FAQ
        Optional<Faq> faqOpt = puntuacionService.obtenerMejorFaq(textoNormalizado);
        if (faqOpt.isPresent()) {
            respuesta = faqOpt.get().getRespuesta();
            intencionDetectada = "CONSULTAR_FAQ";
        } else {
            // 4. Sin intención ni FAQ
            respuesta = manejarPreguntaSinRespuesta(textoUsuario, "DESCONOCIDA");
            intencionDetectada = "DESCONOCIDA";
        }
    }
}

            // 5. Guardar respuesta
            guardarMensaje(conversacion, "BOT", respuesta);
            guardarMensajeConIntencion(conversacion, "BOT", respuesta, intencionDetectada);

            // 6. Actualizar contexto
            String entidadPrincipal = entidadesExtraidas.containsKey("grado") ? 
                (String) entidadesExtraidas.get("grado") : null;
            contextoService.actualizarContexto(
                conversacion,
                intencionDetectada,
                entidadPrincipal,
                textoUsuario,
                respuesta
            );

            // 7. Construir respuesta estructurada
            return construirRespuestaEstructurada(respuesta, intencionDetectada, entidadesExtraidas);

        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ChatbotException("No se pudo procesar el mensaje del chatbot", ex);
        }
    }

    private ChatbotRespuesta construirRespuestaEstructurada(String respuesta, String intencion, Map<String, Object> entidades) {
        ChatbotRespuesta respuestaDTO = new ChatbotRespuesta();
        respuestaDTO.setRespuesta(respuesta);
        respuestaDTO.setIntencion(intencion);
        respuestaDTO.setEntidades(entidades);
        
        // Determinar si mostrar opciones adicionales
        if (intencion.equals("CONSULTAR_EVENTOS")) {
            respuestaDTO.setOpciones(Arrays.asList("Ver todos los eventos", "Ver eventos por mes"));
        } else if (intencion.equals("CONSULTAR_NOTICIAS")) {
            respuestaDTO.setOpciones(Arrays.asList("Ver todas las noticias", "Ver noticias por categoría"));
        } else if (intencion.equals("CONSULTAR_DOCUMENTOS")) {
            respuestaDTO.setOpciones(Arrays.asList("Ver todos los documentos", "Buscar documentos"));
        }
        
        return respuestaDTO;
    }

    private String responderConContexto(String texto, ContextoConversacion contexto, Conversacion conversacion) {
        String ultimaIntencion = contexto.getUltimaIntencion();
        
        if (ultimaIntencion == null) {
            return "No entiendo la referencia a lo anterior. ¿Podrías reformular tu pregunta?";
        }

        // Buscar la intención por nombre
        Optional<Intencion> intencionOpt = intencionService.obtenerPorNombre(ultimaIntencion);
        
        if (intencionOpt.isPresent()) {
            // Extraer entidades de la pregunta de contexto
            Map<String, Object> entidades = extractorDatos.extraerEntidades(texto);
            return procesarPorIntencion(intencionOpt.get(), texto, entidades);
        }

        return "Lo siento, no pude entender la referencia a la conversación anterior.";
    }

    private String procesarPorIntencion(Intencion intencion, String texto, Map<String, Object> entidades) {
    String nombre = intencion.getNombre();
    log.info("Procesando intención: {}", nombre);
    
    switch (nombre) {
        case "CONSULTAR_EVENTOS":
            return responderEventos(texto, entidades);
        case "CONSULTAR_NOTICIAS":
            return responderNoticias(texto, entidades);
        case "CONSULTAR_DOCUMENTOS":
            return responderDocumentos(texto, entidades);
        case "CONSULTAR_SEDES":
            return responderSedes(texto, entidades);
        case "CONSULTAR_CONTACTOS":
            return responderContactos(texto, entidades);
        case "CONSULTAR_HORARIOS":
            return responderHorarios(texto, entidades);
        case "CONSULTAR_SERVICIOS":
            return responderServicios(texto, entidades);
        case "CONSULTAR_INSTITUCION":
            return responderInstitucion(texto, entidades);
        default:
            log.warn("Intención no reconocida: {}", nombre);
            return manejarPreguntaSinRespuesta(texto, nombre);
    }
}

    private String responderEventos(String texto, Map<String, Object> entidades) {
        LocalDate fechaInicio = LocalDate.now();
        List<Evento> eventos = eventoRepository.findByFechaGreaterThanEqualOrderByFechaAsc(fechaInicio);
        
        if (eventos == null || eventos.isEmpty()) {
            return "Actualmente no hay eventos programados próximos en la institución.";
        }

        // Filtrar por tipo de evento si se detectó
        if (entidades.containsKey("tipo_evento")) {
            String tipo = (String) entidades.get("tipo_evento");
            eventos = eventos.stream()
                .filter(e -> e.getTitulo() != null && 
                    e.getTitulo().toLowerCase().contains(tipo.toLowerCase()))
                .collect(Collectors.toList());
        }

        // Filtrar por grado si se detectó
        if (entidades.containsKey("grado")) {
            String grado = (String) entidades.get("grado");
            eventos = eventos.stream()
                .filter(e -> e.getDescripcion() != null && 
                    e.getDescripcion().toLowerCase().contains(grado.toLowerCase()))
                .collect(Collectors.toList());
        }

        if (eventos.isEmpty()) {
            return "No encontré eventos que coincidan con tu búsqueda.";
        }

        // Limitar resultados
        eventos = eventos.stream().limit(MAX_RESULTADOS).collect(Collectors.toList());

        StringBuilder respuesta = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (eventos.size() == 1) {
            Evento e = eventos.get(0);
            respuesta.append("📅 **").append(e.getTitulo()).append("**\n");
            if (e.getDescripcion() != null) respuesta.append("📝 ").append(e.getDescripcion()).append("\n");
            respuesta.append("📆 ").append(e.getFecha().format(formatter));
            if (e.getHoraInicio() != null) {
                respuesta.append(" 🕒 ").append(e.getHoraInicio());
                if (e.getHoraFin() != null) {
                    respuesta.append(" - ").append(e.getHoraFin());
                }
            }
            if (e.getLugar() != null) respuesta.append("\n📍 ").append(e.getLugar());
        } else {
            respuesta.append("📅 **Eventos próximos:**\n\n");
            for (Evento e : eventos) {
                respuesta.append("• **").append(e.getTitulo()).append("**\n");
                respuesta.append("  📆 ").append(e.getFecha().format(formatter));
                if (e.getLugar() != null) respuesta.append(" | 📍 ").append(e.getLugar());
                respuesta.append("\n");
            }
        }

        return respuesta.toString();
    }

    private String responderNoticias(String texto, Map<String, Object> entidades) {
        List<Noticia> noticias = noticiaRepository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADA");
        
        if (noticias == null || noticias.isEmpty()) {
            return "No hay novedades publicadas en este momento.";
        }

        noticias = noticias.stream().limit(MAX_RESULTADOS).collect(Collectors.toList());

        StringBuilder respuesta = new StringBuilder("📰 **Novedades recientes:**\n\n");
        
        for (Noticia n : noticias) {
            respuesta.append("• **").append(n.getTitulo()).append("**\n");
            if (n.getResumen() != null) {
                respuesta.append("  ").append(n.getResumen()).append("\n");
            }
            if (n.getFechaPublicacion() != null) {
                respuesta.append("  📆 ").append(n.getFechaPublicacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            respuesta.append("\n");
        }

        return respuesta.toString();
    }

    private String responderDocumentos(String texto, Map<String, Object> entidades) {
        List<Documento> documentos = documentoRepository.findByEstado("ACTIVO");
        
        if (documentos == null || documentos.isEmpty()) {
            return "No hay documentos activos disponibles en este momento.";
        }

        // Filtrar por tipo de documento si se detectó
        if (entidades.containsKey("tipo_documento")) {
            String tipo = (String) entidades.get("tipo_documento");
            documentos = documentos.stream()
                .filter(d -> d.getTipo() != null && d.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
        }

        if (documentos.isEmpty()) {
            return "No encontré documentos que coincidan con tu búsqueda.";
        }

        documentos = documentos.stream().limit(MAX_RESULTADOS).collect(Collectors.toList());

        StringBuilder respuesta = new StringBuilder("📄 **Documentos disponibles:**\n\n");
        
        for (Documento d : documentos) {
            respuesta.append("• **").append(d.getTitulo()).append("**\n");
            if (d.getDescripcion() != null) {
                respuesta.append("  ").append(d.getDescripcion()).append("\n");
            }
            if (d.getTipo() != null) {
                respuesta.append("  📎 ").append(d.getTipo());
            }
            if (d.getRutaArchivo() != null) {
                respuesta.append(" | [📥 Descargar](").append(d.getRutaArchivo()).append(")");
            }
            respuesta.append("\n");
        }

        return respuesta.toString();
    }

    private String responderSedes(String texto, Map<String, Object> entidades) {
        List<Sede> sedes = sedeRepository.findByActivaTrue();
        
        if (sedes == null || sedes.isEmpty()) {
            return "No hay sedes registradas en el sistema.";
        }

        StringBuilder respuesta = new StringBuilder("🏫 **Sedes de la institución:**\n\n");
        
        for (Sede s : sedes) {
            respuesta.append("• **").append(s.getNombre()).append("**\n");
            if (s.getDireccion() != null) {
                respuesta.append("  📍 ").append(s.getDireccion()).append("\n");
            }
            if (s.getTelefono() != null) {
                respuesta.append("  📞 ").append(s.getTelefono()).append("\n");
            }
            if (s.getHorarioAtencion() != null) {
                respuesta.append("  🕐 ").append(s.getHorarioAtencion()).append("\n");
            }
            respuesta.append("\n");
        }

        return respuesta.toString();
    }

    private String responderContactos(String texto, Map<String, Object> entidades) {
        List<Contacto> contactos = contactoRepository.findAll();
        
        if (contactos == null || contactos.isEmpty()) {
            return "No hay contactos registrados en el sistema.";
        }

        // Filtrar por tipo de contacto si se detectó
        if (entidades.containsKey("tipo_contacto")) {
            String tipo = (String) entidades.get("tipo_contacto");
            contactos = contactos.stream()
                .filter(c -> c.getTipo() != null && c.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
        }

        if (contactos.isEmpty()) {
            return "No encontré contactos que coincidan con tu búsqueda.";
        }

        StringBuilder respuesta = new StringBuilder("📞 **Contactos institucionales:**\n\n");
        
        for (Contacto c : contactos) {
            String icono = switch (c.getTipo() != null ? c.getTipo().toUpperCase() : "") {
                case "TELEFONO" -> "📞";
                case "CORREO" -> "✉️";
                case "DIRECCION" -> "📍";
                default -> "📌";
            };
            
            respuesta.append("• ").append(icono).append(" **");
            if (c.getArea() != null && c.getArea().getNombre() != null) {
                respuesta.append(c.getArea().getNombre()).append(" - ");
            }
            respuesta.append(c.getTipo()).append("**\n");
            respuesta.append("  ").append(c.getValor());
            if (c.getDescripcion() != null) {
                respuesta.append(" (").append(c.getDescripcion()).append(")");
            }
            respuesta.append("\n\n");
        }

        return respuesta.toString();
    }

    private String responderHorarios(String texto, Map<String, Object> entidades) {
        // Buscar en FAQ específicas de horarios
        List<Faq> faqs = faqRepository.findByActivaTrue();
        List<Faq> horariosFaq = faqs.stream()
            .filter(f -> f.getCategoria() != null && 
                (f.getCategoria().equalsIgnoreCase("HORARIOS") || 
                 f.getCategoria().equalsIgnoreCase("HORARIO")))
            .collect(Collectors.toList());

        if (!horariosFaq.isEmpty()) {
            StringBuilder respuesta = new StringBuilder("🕐 **Horarios de atención:**\n\n");
            for (Faq f : horariosFaq) {
                respuesta.append("• ").append(f.getPregunta()).append("\n");
                respuesta.append("  ").append(f.getRespuesta()).append("\n\n");
            }
            return respuesta.toString();
        }

        return "Los horarios de atención de la institución son los publicados en la web institucional. " +
               "Si quieres, también puedo ayudarte con información de sedes o servicios específicos.";
    }

    private String responderServicios(String texto, Map<String, Object> entidades) {
        return "La institución ofrece los siguientes servicios:\n\n" +
               "• 📚 **Biblioteca** - Lunes a viernes 8:00 AM - 6:00 PM\n" +
               "• 💻 **Sala de cómputo** - Lunes a viernes 7:00 AM - 5:00 PM\n" +
               "• 🍽️ **Cafetería** - Lunes a viernes 7:00 AM - 4:00 PM\n" +
               "• 🏥 **Enfermería** - Lunes a viernes 8:00 AM - 4:00 PM\n\n" +
               "Para más información sobre servicios específicos, contáctanos directamente.";
    }

    private String responderInstitucion(String texto, Map<String, Object> entidades) {
        return "🏫 **Información institucional:**\n\n" +
               "Somos una institución educativa comprometida con la formación integral de nuestros estudiantes.\n\n" +
               "📞 **Contacto general:** (123) 456-7890\n" +
               "✉️ **Correo:** info@institucion.edu.co\n" +
               "🌐 **Web:** www.institucion.edu.co\n\n" +
               "¿Hay algo específico que quieras saber sobre la institución?";
    }

    private String manejarPreguntaSinRespuesta(String texto, String intencionDetectada) {
        PreguntaSinRespuesta psr = new PreguntaSinRespuesta();
        psr.setPregunta(texto);
        psr.setIntentoIntencion(intencionDetectada);
        sinRespuestaRepository.save(psr);

        return "🔍 No encontré información sobre tu consulta.\n\n" +
               "La he registrado para que los administradores puedan mejorar mi base de conocimiento. " +
               "Mientras tanto, puedes intentar reformular tu pregunta o consultar nuestra página web.";
    }

    private void validarEntrada(String sessionId, String textoUsuario) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new BadRequestException("sessionId es obligatorio");
        }
        if (textoUsuario == null || textoUsuario.isBlank()) {
            throw new BadRequestException("El texto del usuario no puede estar vacío");
        }
    }

    private Conversacion obtenerOCrearConversacion(String sessionId) {
        return conversacionRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Conversacion nuevaConv = new Conversacion();
                    nuevaConv.setSessionId(sessionId);
                    nuevaConv.setEstado("ACTIVA");
                    return conversacionRepository.save(nuevaConv);
                });
    }

    private void guardarMensaje(Conversacion conversacion, String tipo, String contenido) {
        Mensaje mensaje = new Mensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setTipo(tipo);
        mensaje.setContenido(contenido);
        mensajeRepository.save(mensaje);
    }

    private void guardarMensajeConIntencion(Conversacion conversacion, String tipo, String contenido, String intencion) {
        Mensaje mensaje = new Mensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setTipo(tipo);
        mensaje.setContenido(contenido);
        mensaje.setIntencionDetectada(intencion);
        mensajeRepository.save(mensaje);
    }
}