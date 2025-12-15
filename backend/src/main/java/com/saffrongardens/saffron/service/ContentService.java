package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.ContentPage;
import com.saffrongardens.saffron.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ContentService {

    private final ContentRepository contentRepository;
    private final AuditService auditService;

    public ContentService(ContentRepository contentRepository, AuditService auditService) {
        this.contentRepository = contentRepository;
        this.auditService = auditService;
    }

    public ContentPage create(ContentPage page, String actor) {
        page.setCreatedAt(Instant.now());
        page.setUpdatedAt(Instant.now());
        ContentPage saved = contentRepository.save(page);
        auditService.record(actor == null ? "SYSTEM" : actor, "CREATE_CONTENT", "Created content slug=" + page.getSlug());
        return saved;
    }

    public ContentPage update(Long id, ContentPage updated, String actor) {
        ContentPage existing = contentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        existing.setTitle(updated.getTitle());
        existing.setBody(updated.getBody());
        existing.setPublished(updated.isPublished());
        existing.setUpdatedAt(Instant.now());
        ContentPage saved = contentRepository.save(existing);
        auditService.record(actor == null ? "SYSTEM" : actor, "UPDATE_CONTENT", "Updated content id=" + id);
        return saved;
    }

    public void delete(Long id, String actor) {
        contentRepository.deleteById(id);
        auditService.record(actor == null ? "SYSTEM" : actor, "DELETE_CONTENT", "Deleted content id=" + id);
    }

    public ContentPage getBySlug(String slug) {
        return contentRepository.findBySlug(slug).orElse(null);
    }

    public List<ContentPage> listAll() {
        return contentRepository.findAll();
    }
}
