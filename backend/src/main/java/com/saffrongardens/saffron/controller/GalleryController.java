package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.GalleryItem;
import com.saffrongardens.saffron.service.GalleryService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('EVENT_PLANNER','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "description", required = false) String description,
                                    Authentication authentication) throws IOException {
        String username = authentication != null ? authentication.getName() : null;
        GalleryItem item = galleryService.upload(file, title, description, username);
        return ResponseEntity.status(201).body(item);
    }

    @GetMapping("/list")
    public List<GalleryItem> list() {
        return galleryService.listAll();
    }

    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<?> file(@PathVariable String filename) throws MalformedURLException {
        Resource r = galleryService.loadAsResource(filename);
        if (r == null) return ResponseEntity.notFound().build();
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + r.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(r);
    }
}
