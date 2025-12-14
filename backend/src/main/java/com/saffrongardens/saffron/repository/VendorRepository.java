package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findByCategory(String category);
    List<Vendor> findByCategoryAndUserApprovedTrue(String category);
    java.util.Optional<Vendor> findByUserUsername(String username);
    List<Vendor> findByUserApprovedTrue();
}
