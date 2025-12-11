package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.GalleryItem;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.GalleryRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;

    private final Path storagePath;
    private final AuditService auditService;

    public GalleryService(GalleryRepository galleryRepository, UserRepository userRepository,
                          @Value("${app.storage.gallery:uploads/gallery}") String galleryDir,
                          AuditService auditService) throws IOException {
        this.galleryRepository = galleryRepository;
        this.userRepository = userRepository;
        this.storagePath = Paths.get(galleryDir).toAbsolutePath();
        this.auditService = auditService;
        Files.createDirectories(this.storagePath);
    }

    public GalleryItem upload(MultipartFile file, String title, String description, String username) throws IOException {
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String stored = UUID.randomUUID().toString() + ext;
        Path dest = storagePath.resolve(stored);
        Files.copy(file.getInputStream(), dest);

        GalleryItem g = new GalleryItem();
        g.setTitle(title != null ? title : original);
        g.setDescription(description);
        g.setFilename(stored);
        g.setCreatedAt(Instant.now());
        if (username != null) {
            userRepository.findByUsername(username).ifPresent(g::setUploadedBy);
        }
        g = galleryRepository.save(g);
        auditService.record(username == null ? "SYSTEM" : username, "GALLERY_UPLOAD", "Uploaded gallery item id=" + g.getId());
        return g;
    }

    public Resource loadAsResource(String filename) throws MalformedURLException {
        Path file = storagePath.resolve(filename).normalize();
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists()) return resource;
        return null;
    }

    public List<GalleryItem> listAll() {
        return galleryRepository.findAll();
    }
}
