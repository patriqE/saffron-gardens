package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.ContentPage;
import com.saffrongardens.saffron.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        ContentPage page = contentService.getBySlug(slug);
        if (page == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(page);
    }

    @GetMapping("/list")
    public List<ContentPage> list() { return contentService.listAll(); }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody ContentPage page, Authentication auth) {
        String actor = auth != null ? auth.getName() : "SYSTEM";
        ContentPage saved = contentService.create(page, actor);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ContentPage page, Authentication auth) {
        String actor = auth != null ? auth.getName() : "SYSTEM";
        ContentPage saved = contentService.update(id, page, actor);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        String actor = auth != null ? auth.getName() : "SYSTEM";
        contentService.delete(id, actor);
        return ResponseEntity.noContent().build();
    }
}
