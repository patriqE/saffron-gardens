package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.Planner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlannerRepository extends JpaRepository<Planner, Long> {
    java.util.Optional<Planner> findByUserUsername(String username);
}

