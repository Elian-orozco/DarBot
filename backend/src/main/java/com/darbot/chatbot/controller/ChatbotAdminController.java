package com.darbot.chatbot.controller;

import com.darbot.chatbot.entity.Faq;
import com.darbot.chatbot.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/chatbot")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ChatbotAdminController {

    private final FaqRepository faqRepository;

    @GetMapping("/faqs")
    public ResponseEntity<List<Faq>> listarFaqs() {
        return ResponseEntity.ok(faqRepository.findAll());
    }

    @PostMapping("/faqs")
    public ResponseEntity<Faq> crearFaq(@RequestBody Faq faq) {
        return ResponseEntity.ok(faqRepository.save(faq));
    }

    @PutMapping("/faqs/{id}")
    public ResponseEntity<Faq> actualizarFaq(@PathVariable Long id, @RequestBody Faq faqActualizada) {
        Faq faq = faqRepository.findById(id).orElseThrow();
        faq.setPregunta(faqActualizada.getPregunta());
        faq.setRespuesta(faqActualizada.getRespuesta());
        faq.setCategoria(faqActualizada.getCategoria());
        faq.setActiva(faqActualizada.getActiva());
        return ResponseEntity.ok(faqRepository.save(faq));
    }

    @DeleteMapping("/faqs/{id}")
    public ResponseEntity<Void> eliminarFaq(@PathVariable Long id) {
        faqRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
