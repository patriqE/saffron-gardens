package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.PortfolioFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioFileRepository extends JpaRepository<PortfolioFile, Long> {
    List<PortfolioFile> findByUserUsername(String username);
}

