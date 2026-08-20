package com.darbot.contenidos.service;

import com.darbot.contenidos.entity.Documento;
import com.darbot.contenidos.entity.Evento;
import com.darbot.contenidos.entity.Noticia;
import com.darbot.contenidos.repository.DocumentoRepository;
import com.darbot.contenidos.repository.EventoRepository;
import com.darbot.contenidos.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContenidoService {

    private final NoticiaRepository noticiaRepository;
    private final EventoRepository eventoRepository;
    private final DocumentoRepository documentoRepository;

    // Noticias
    public List<Noticia> obtenerNoticiasPublicadas() {
        return noticiaRepository.findByEstadoOrderByFechaPublicacionDesc("PUBLICADA");
    }
    public Noticia guardarNoticia(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }

    // Eventos
    public List<Evento> obtenerProximosEventos() {
        return eventoRepository.findByFechaGreaterThanEqualOrderByFechaAsc(LocalDate.now());
    }
    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    // Documentos
    public List<Documento> obtenerDocumentosActivos() {
        return documentoRepository.findByEstado("ACTIVO");
    }
    public Documento guardarDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }
}