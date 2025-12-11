package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.controller.dto.CreateBookingRequest;
import com.saffrongardens.saffron.controller.dto.VendorAssignmentRequest;
import com.saffrongardens.saffron.entity.*;
import com.saffrongardens.saffron.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HallRepository hallRepository;
    private final UserRepository userRepository;
    private final VendorAssignmentRepository vendorAssignmentRepository;
    private final AvailabilityService availabilityService;
    private final AuditService auditService;
    private final com.saffrongardens.saffron.repository.PaymentRecordRepository paymentRecordRepository;

    public BookingService(BookingRepository bookingRepository, HallRepository hallRepository, UserRepository userRepository,
                          VendorAssignmentRepository vendorAssignmentRepository, AvailabilityService availabilityService, AuditService auditService,
                          com.saffrongardens.saffron.repository.PaymentRecordRepository paymentRecordRepository) {
        this.bookingRepository = bookingRepository;
        this.hallRepository = hallRepository;
        this.userRepository = userRepository;
        this.vendorAssignmentRepository = vendorAssignmentRepository;
        this.availabilityService = availabilityService;
        this.auditService = auditService;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    public Booking createBooking(CreateBookingRequest req) {
        Hall hall = hallRepository.findById(req.getHallId()).orElseThrow(() -> new IllegalArgumentException("Hall not found"));
        // ensure availability for requested slot (must be free of confirmed bookings)
        if (!availabilityService.isAvailable(hall.getId(), req.getStartTime(), req.getEndTime())) {
            throw new IllegalArgumentException("Hall not available for requested time");
        }

        String actor = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;
        User planner = userRepository.findByUsername(actor).orElseThrow(() -> new IllegalArgumentException("Planner user not found"));

        Booking booking = new Booking();
        booking.setHall(hall);
        booking.setPlanner(planner);
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        booking.setTotalAmount(req.getTotalAmount());
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        List<VendorAssignment> savedAssignments = new ArrayList<>();
        if (req.getVendorAssignments() != null) {
            for (VendorAssignmentRequest vr : req.getVendorAssignments()) {
                VendorAssignment a = new VendorAssignment();
                a.setBooking(booking);
                userRepository.findById(vr.getVendorId()).ifPresent(a::setVendor);
                a.setVendorType(vr.getVendorType());
                a.setNotes(vr.getNotes());
                a.setPrice(vr.getPrice());
                a.setStatus(AssignmentStatus.PENDING);
                savedAssignments.add(vendorAssignmentRepository.save(a));
            }
        }

        auditService.record(actor == null ? "SYSTEM" : actor, "CREATE_BOOKING", "Booking id=" + booking.getId());
        return booking;
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public Booking confirmBooking(Long bookingId, String actor) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        // if payment required, ensure there's a successful payment record
        if (booking.getTotalAmount() != null && booking.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            // look for successful payment record for this booking
            java.util.List<com.saffrongardens.saffron.entity.PaymentRecord> payments = paymentRecordRepository.findByBookingId(bookingId);
            boolean paid = payments.stream().anyMatch(p -> p.getStatus() != null && p.getStatus().toLowerCase().contains("success"));
            if (!paid) {
                throw new IllegalStateException("Payment not completed for booking");
            }
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(java.time.Instant.now());
        Booking saved = bookingRepository.save(booking);
        auditService.record(actor == null ? "SYSTEM" : actor, "CONFIRM_BOOKING", "Confirmed booking id=" + bookingId);
        return saved;
    }

    public VendorAssignment addVendorAssignment(Long bookingId, VendorAssignmentRequest req, String actor) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        // ensure caller is planner
        String caller = actor;
        if (caller == null || booking.getPlanner() == null || !caller.equals(booking.getPlanner().getUsername())) {
            throw new SecurityException("Only the booking's planner may assign vendors");
        }
        VendorAssignment a = new VendorAssignment();
        a.setBooking(booking);
        userRepository.findById(req.getVendorId()).ifPresent(a::setVendor);
        a.setVendorType(req.getVendorType());
        a.setNotes(req.getNotes());
        a.setPrice(req.getPrice());
        a.setStatus(AssignmentStatus.PENDING);
        VendorAssignment saved = vendorAssignmentRepository.save(a);
        auditService.record(caller, "ASSIGN_VENDOR", "Assigned vendor id=" + req.getVendorId() + " to booking=" + bookingId);
        return saved;
    }

    public void removeVendorAssignment(Long bookingId, Long assignmentId, String actor) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        String caller = actor;
        if (caller == null || booking.getPlanner() == null || !caller.equals(booking.getPlanner().getUsername())) {
            throw new SecurityException("Only the booking's planner may remove vendor assignments");
        }
        VendorAssignment a = vendorAssignmentRepository.findById(assignmentId).orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        if (!a.getBooking().getId().equals(bookingId)) throw new IllegalArgumentException("Assignment does not belong to booking");
        vendorAssignmentRepository.delete(a);
        auditService.record(caller, "REMOVE_VENDOR_ASSIGNMENT", "Removed assignment id=" + assignmentId + " from booking=" + bookingId);
    }
}
