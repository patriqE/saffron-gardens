package com.saffrongardens.saffron.repository;

import com.saffrongardens.saffron.entity.VendorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VendorAssignmentRepository extends JpaRepository<VendorAssignment, Long> {
    List<VendorAssignment> findByBookingId(Long bookingId);

    @org.springframework.data.jpa.repository.Query("select va from VendorAssignment va where va.vendor.id = :vendorUserId and va.booking.startTime < :end and va.booking.endTime > :start and va.booking.status = :status")
    java.util.List<VendorAssignment> findConflictingForVendor(@org.springframework.data.repository.query.Param("vendorUserId") Long vendorUserId, @org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end, @org.springframework.data.repository.query.Param("status") com.saffrongardens.saffron.entity.BookingStatus status);
}
