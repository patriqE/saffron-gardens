package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.dto.VendorApplicationDTO;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.entity.Vendor;
import com.saffrongardens.saffron.repository.UserRepository;
import com.saffrongardens.saffron.repository.VendorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
@Transactional
public class VendorService {

    private final UserRepository userRepo;

    private final VendorRepository vendorRepo;

    private final PasswordEncoder passwordEncoder;

    private final AuditService auditService;
    private final com.saffrongardens.saffron.repository.VendorAssignmentRepository vendorAssignmentRepository;

    public VendorService(UserRepository userRepo, VendorRepository vendorRepo, PasswordEncoder passwordEncoder, AuditService auditService, com.saffrongardens.saffron.repository.VendorAssignmentRepository vendorAssignmentRepository) {
        this.userRepo = userRepo;
        this.vendorRepo = vendorRepo;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.vendorAssignmentRepository = vendorAssignmentRepository;
    }

    public void apply(VendorApplicationDTO dto) {
        // encode the password before storing
        String encoded = passwordEncoder.encode(dto.getPassword());
        User user = new User(dto.getUsername(), encoded, "VENDOR");
        user.setApproved(false);
        userRepo.save(user);

        Vendor vendor = new Vendor();
        vendor.setUser(user);
        vendor.setBusinessName(dto.getBusinessName());
        vendor.setCategory(dto.getCategory());
        vendor.setDescription(dto.getDescription());
        // store contact email on vendor record as well
        vendor.setEmail(dto.getEmail());
        vendorRepo.save(vendor);

        String actor = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM";
        auditService.record(actor, "VENDOR_APPLY", "Vendor application created for " + dto.getUsername());
    }

    public List<Vendor> getVendorsByCategory(String category) {
        // Only return vendors whose user account is approved
        return vendorRepo.findByCategoryAndUserApprovedTrue(category);
    }

    public Vendor getVendorForUser(String username) {
        return vendorRepo.findByUserUsername(username).orElse(null);
    }

    public Vendor updateVendorProfile(String username, com.saffrongardens.saffron.controller.dto.VendorProfileDTO updated) {
        Vendor existing = getVendorForUser(username);
        if (existing == null) throw new jakarta.persistence.EntityNotFoundException("Vendor not found for user");
        // update allowed fields from DTO
        existing.setBusinessName(updated.getBusinessName());
        existing.setCategory(updated.getCategory());
        existing.setDescription(updated.getDescription());
        existing.setWebsite(updated.getWebsite());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        existing.setCity(updated.getCity());
        existing.setState(updated.getState());
        existing.setLogoPath(updated.getLogoPath());
        existing.setDocumentsPath(updated.getDocumentsPath());
        return vendorRepo.save(existing);
    }

    public java.util.List<Vendor> listApprovedVendors(String category) {
        if (category == null || category.isBlank()) return vendorRepo.findByUserApprovedTrue();
        return vendorRepo.findByCategoryAndUserApprovedTrue(category);
    }

    public boolean isVendorAvailable(Long vendorUserId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        java.util.List<com.saffrongardens.saffron.entity.VendorAssignment> conflicts = vendorAssignmentRepository.findConflictingForVendor(vendorUserId, start, end, com.saffrongardens.saffron.entity.BookingStatus.CONFIRMED);
        return conflicts == null || conflicts.isEmpty();
    }

    public void approveVendor(Long vendorId) {
        Vendor vendor = vendorRepo.findById(vendorId)
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Vendor not found"));
        User user = vendor.getUser();
        user.setApproved(true);
        userRepo.save(user);

        String actor = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : "SYSTEM";
        auditService.record(actor, "APPROVE_VENDOR", "Approved vendor id=" + vendorId + " user=" + user.getUsername());
    }

    public List<Vendor> findAll() {
        return vendorRepo.findAll();
    }
}
