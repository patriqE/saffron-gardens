package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.controller.dto.CreateBookingRequest;
import com.saffrongardens.saffron.entity.Booking;
import com.saffrongardens.saffron.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('EVENT_PLANNER')")
    public ResponseEntity<?> create(@RequestBody CreateBookingRequest req) {
        Booking created = bookingService.createBooking(req);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/{id}/vendors")
    @PreAuthorize("hasRole('EVENT_PLANNER')")
    public ResponseEntity<?> addVendorToBooking(@PathVariable Long id, @RequestBody com.saffrongardens.saffron.controller.dto.VendorAssignmentRequest req, Authentication auth) {
        try {
            com.saffrongardens.saffron.entity.VendorAssignment assignment = bookingService.addVendorAssignment(id, req, auth.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}/vendors/{assignmentId}")
    @PreAuthorize("hasRole('EVENT_PLANNER')")
    public ResponseEntity<?> removeVendorFromBooking(@PathVariable Long id, @PathVariable Long assignmentId, Authentication auth) {
        try {
            bookingService.removeVendorAssignment(id, assignmentId, auth.getName());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Booking b = bookingService.findById(id);
        if (b == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(b);
    }
}
