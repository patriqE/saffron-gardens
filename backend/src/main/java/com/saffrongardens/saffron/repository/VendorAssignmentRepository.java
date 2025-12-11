package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.VendorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorAssignmentRepository extends JpaRepository<VendorAssignment, Long> {
    List<VendorAssignment> findByBookingId(Long bookingId);
}
