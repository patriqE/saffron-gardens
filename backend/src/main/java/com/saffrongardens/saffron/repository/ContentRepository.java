package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.ContentPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentRepository extends JpaRepository<ContentPage, Long> {
    Optional<ContentPage> findBySlug(String slug);
}
