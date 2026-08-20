package com.darbot.chatbot.service;

import com.darbot.chatbot.entity.Conversacion;
import com.darbot.chatbot.entity.Faq;
import com.darbot.chatbot.entity.Mensaje;
import com.darbot.chatbot.entity.PreguntaSinRespuesta;
import com.darbot.chatbot.repository.ConversacionRepository;
import com.darbot.chatbot.repository.FaqRepository;
import com.darbot.chatbot.repository.MensajeRepository;
import com.darbot.chatbot.repository.PreguntaSinRespuestaRepository;
import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.repository.DocumentoRepository;
import com.darbot.contenidos.repository.EventoRepository;
import com.darbot.contenidos.repository.NoticiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private FaqRepository faqRepository;

    @Mock
    private ConversacionRepository conversacionRepository;

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private PreguntaSinRespuestaRepository sinRespuestaRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private NoticiaRepository noticiaRepository;

    @Mock
    private DocumentoRepository documentoRepository;

    @InjectMocks
    private ChatbotService chatbotService;

    @BeforeEach
    void setUp() {
        when(mensajeRepository.save(any(Mensaje.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(conversacionRepository.save(any(Conversacion.class))).thenAnswer(invocation -> {
            Conversacion conversacion = invocation.getArgument(0);
            if (conversacion.getId() == null) {
                conversacion.setId(1L);
            }
            return conversacion;
        });
        when(conversacionRepository.findBySessionId("s-123")).thenReturn(Optional.empty());
    }

    @Test
    void debeResolverFaqConTerminosRelacionados() {
        Faq faq = new Faq();
        faq.setPregunta("¿Cómo puedo comunicarme con la institución?");
        faq.setRespuesta("Puedes escribirnos al correo institucional o llamar a coordinación.");
        faq.setActiva(true);

        when(faqRepository.findByActivaTrue()).thenReturn(List.of(faq));

        String respuesta = chatbotService.procesarMensaje("s-123", "Necesito contacto con la escuela");

        assertThat(respuesta).contains("correo institucional");
    }

    @Test
    void debeResponderNoticiasCuandoPreguntaPorNovedades() {
        Noticia noticia1 = new Noticia();
        noticia1.setTitulo("Matrículas abiertas");
        noticia1.setResumen("Proceso de inscripción");
        noticia1.setContenido("Contenido");
        noticia1.setEstado("PUBLICADA");
        noticia1.setFechaPublicacion(LocalDateTime.now());

        Noticia noticia2 = new Noticia();
        noticia2.setTitulo("Festival escolar");
        noticia2.setResumen("Actividades del mes");
        noticia2.setContenido("Contenido");
        noticia2.setEstado("PUBLICADA");
        noticia2.setFechaPublicacion(LocalDateTime.now().minusDays(1));

        when(noticiaRepository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADA"))
                .thenReturn(List.of(noticia1, noticia2));

        String respuesta = chatbotService.procesarMensaje("s-123", "¿Qué novedades hay en la institución?");

        assertThat(respuesta).contains("Matrículas abiertas");
        assertThat(respuesta).contains("Festival escolar");
    }

    @Test
    void debeRegistrarPreguntaSinRespuestaCuandoNoHayCoincidencias() {
        when(faqRepository.findByActivaTrue()).thenReturn(List.of());
        when(sinRespuestaRepository.save(any(PreguntaSinRespuesta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String respuesta = chatbotService.procesarMensaje("s-123", "¿Dónde está el laboratorio de química?");

        assertThat(respuesta).contains("aún no tengo una respuesta");
    }
}
