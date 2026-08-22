package com.darbot.chatbot.service;

import com.darbot.chatbot.dto.ChatbotRespuesta;
import com.darbot.chatbot.entity.*;
import com.darbot.chatbot.repository.*;
import com.darbot.chatbot.util.ExtractorDatos;
import com.darbot.chatbot.util.LenguajeUtil;
import com.darbot.chatbot.util.NormalizadorTexto;
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
import lombok.extern.slf4j.Slf4j;
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
    private final ContextoConversacionRepository contextoRepository;

    private final IntencionService intencionService;
    private final ContextoService contextoService;
    private final PuntuacionService puntuacionService;
    private final NormalizadorTexto normalizador;
    private final ExtractorDatos extractorDatos;
    private final LenguajeUtil lenguajeUtil;

    private static final int MAX_RESULTADOS = 5;

    public ChatbotRespuesta procesarMensaje(String sessionId, String textoUsuario) {
        validarEntrada(sessionId, textoUsuario);

        try {
            Conversacion conversacion = obtenerOCrearConversacion(sessionId);
            guardarMensaje(conversacion, "USER", textoUsuario);

            // Normalización avanzada con LenguajeUtil
            String textoNormalizado = lenguajeUtil.normalizar(textoUsuario);
            log.info("Texto normalizado: '{}'", textoNormalizado);

            // Verificar si es pregunta compuesta
            if (lenguajeUtil.esPreguntaCompuesta(textoNormalizado)) {
                String respuesta = responderPreguntaCompuesta(textoNormalizado);
                guardarMensajeConIntencion(conversacion, "BOT", respuesta, "COMPUESTA");
                return construirRespuestaEstructurada(respuesta, "COMPUESTA", new HashMap<>());
            }

            // Verificar negación
            boolean tieneNegacion = lenguajeUtil.contieneNegacion(textoNormalizado);
            String textoSinNegacion = tieneNegacion ? lenguajeUtil.eliminarNegaciones(textoNormalizado) : textoNormalizado;

            // 1. Verificar si es pregunta de contexto
            boolean esContexto = contextoService.esPreguntaDeContexto(textoNormalizado);
            Optional<ContextoConversacion> contextoOpt = contextoService.obtenerContexto(conversacion);
            
            String respuesta;
            String intencionDetectada = "DESCONOCIDA";
            Map<String, Object> entidadesExtraidas = new HashMap<>();

            if (esContexto && contextoOpt.isPresent()) {
                respuesta = responderConContexto(textoNormalizado, contextoOpt.get(), conversacion);
                intencionDetectada = "CONTEXTO_" + contextoOpt.get().getUltimaIntencion();
            } else {
                // 2. Detectar intención
                Optional<Intencion> intencionOpt = intencionService.detectarIntencion(tieneNegacion ? textoSinNegacion : textoNormalizado);
                
                if (intencionOpt.isPresent()) {
                    Intencion intencion = intencionOpt.get();
                    intencionDetectada = intencion.getNombre();
                    
                    // Extraer entidades de la pregunta
                    entidadesExtraidas = extractorDatos.extraerEntidades(textoNormalizado);
                    
                    respuesta = procesarPorIntencion(intencion, textoNormalizado, entidadesExtraidas);
                    
                    // Si tiene negación, filtrar resultados
                    if (tieneNegacion) {
                        if (intencionDetectada.equals("CONSULTAR_EVENTOS")) {
                            respuesta = "No encontré eventos que coincidan con tu búsqueda (excluyendo eventos).";
                        } else if (intencionDetectada.equals("CONSULTAR_NOTICIAS")) {
                            respuesta = "No hay noticias disponibles (excluyendo noticias).";
                        } else {
                            respuesta = "❌ " + respuesta;
                        }
                    }
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
            log.error("Error procesando mensaje", ex);
            throw new ChatbotException("No se pudo procesar el mensaje del chatbot", ex);
        }
    }

    private String responderPreguntaCompuesta(String texto) {
        List<String> partes = lenguajeUtil.dividirPreguntaCompuesta(texto);
        StringBuilder respuesta = new StringBuilder("📌 **Respuesta a tu consulta:**\n\n");
        
        for (String parte : partes) {
            String parteNormalizada = lenguajeUtil.normalizar(parte);
            Optional<Intencion> intencionOpt = intencionService.detectarIntencion(parteNormalizada);
            if (intencionOpt.isPresent()) {
                Map<String, Object> entidades = extractorDatos.extraerEntidades(parteNormalizada);
                String respuestaParcial = procesarPorIntencion(intencionOpt.get(), parteNormalizada, entidades);
                respuesta.append("• ").append(respuestaParcial).append("\n\n");
            } else {
                respuesta.append("• ❌ No pude entender: \"").append(parte).append("\"\n\n");
            }
        }
        
        return respuesta.toString();
    }

    private String generarRespuestaConOpciones(String respuesta, String intencion) {
        List<String> opciones = new ArrayList<>();
        
        if (intencion.equals("CONSULTAR_EVENTOS")) {
            opciones.addAll(Arrays.asList("Ver todos los eventos", "Ver eventos por mes", "Ver eventos por sede"));
        } else if (intencion.equals("CONSULTAR_NOTICIAS")) {
            opciones.addAll(Arrays.asList("Ver todas las noticias", "Ver noticias por categoría"));
        } else if (intencion.equals("CONSULTAR_DOCUMENTOS")) {
            opciones.addAll(Arrays.asList("Ver todos los documentos", "Buscar documentos"));
        } else if (intencion.equals("CONSULTAR_SEDES")) {
            opciones.addAll(Arrays.asList("Ver todas las sedes", "Ver sedes por ubicación"));
        } else if (intencion.equals("CONSULTAR_CONTACTOS")) {
            opciones.addAll(Arrays.asList("Ver todos los contactos", "Contactar por área"));
        }
        
        if (!opciones.isEmpty()) {
            StringBuilder sb = new StringBuilder(respuesta);
            sb.append("\n\n💡 **Opciones:**");
            for (int i = 0; i < opciones.size(); i++) {
                sb.append("\n").append(i + 1).append(". ").append(opciones.get(i));
            }
            sb.append("\n\n_Responde con el número de la opción que te interesa._");
            return sb.toString();
        }
        
        return respuesta;
    }

    private ChatbotRespuesta construirRespuestaEstructurada(String respuesta, String intencion, Map<String, Object> entidades) {
        ChatbotRespuesta respuestaDTO = new ChatbotRespuesta();
        respuestaDTO.setRespuesta(respuesta);
        respuestaDTO.setIntencion(intencion);
        respuestaDTO.setEntidades(entidades);
        
        // Determinar si mostrar opciones adicionales
        List<String> opciones = new ArrayList<>();
        if (intencion.equals("CONSULTAR_EVENTOS")) {
            opciones.addAll(Arrays.asList("Ver todos los eventos", "Ver eventos por mes"));
        } else if (intencion.equals("CONSULTAR_NOTICIAS")) {
            opciones.addAll(Arrays.asList("Ver todas las noticias", "Ver noticias por categoría"));
        } else if (intencion.equals("CONSULTAR_DOCUMENTOS")) {
            opciones.addAll(Arrays.asList("Ver todos los documentos", "Buscar documentos"));
        }
        if (!opciones.isEmpty()) {
            respuestaDTO.setOpciones(opciones);
        }
        
        return respuestaDTO;
    }

    private String responderConContexto(String texto, ContextoConversacion contexto, Conversacion conversacion) {
        String ultimaIntencion = contexto.getUltimaIntencion();
        
        if (ultimaIntencion == null) {
            return "No entiendo la referencia a lo anterior. ¿Podrías reformular tu pregunta?";
        }

        Optional<Intencion> intencionOpt = intencionService.obtenerPorNombre(ultimaIntencion);
        
        if (intencionOpt.isPresent()) {
            Map<String, Object> entidades = extractorDatos.extraerEntidades(texto);
            return procesarPorIntencion(intencionOpt.get(), texto, entidades);
        }

        return "Lo siento, no pude entender la referencia a la conversación anterior.";
    }

    private String procesarPorIntencion(Intencion intencion, String texto, Map<String, Object> entidades) {
        String nombre = intencion.getNombre();
        log.info("Procesando intención: {}", nombre);
        
        String respuesta = switch (nombre) {
            case "CONSULTAR_EVENTOS" -> responderEventos(texto, entidades);
            case "CONSULTAR_NOTICIAS" -> responderNoticias(texto, entidades);
            case "CONSULTAR_DOCUMENTOS" -> responderDocumentos(texto, entidades);
            case "CONSULTAR_SEDES" -> responderSedes(texto, entidades);
            case "CONSULTAR_CONTACTOS" -> responderContactos(texto, entidades);
            case "CONSULTAR_HORARIOS" -> responderHorarios(texto, entidades);
            case "CONSULTAR_SERVICIOS" -> responderServicios(texto, entidades);
            case "CONSULTAR_INSTITUCION" -> responderInstitucion(texto, entidades);
            default -> {
                log.warn("Intención no reconocida: {}", nombre);
                yield manejarPreguntaSinRespuesta(texto, nombre);
            }
        };
        
        return generarRespuestaConOpciones(respuesta, nombre);
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