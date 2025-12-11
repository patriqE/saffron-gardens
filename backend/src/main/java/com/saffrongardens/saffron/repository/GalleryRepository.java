package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryRepository extends JpaRepository<GalleryItem, Long> {

}
