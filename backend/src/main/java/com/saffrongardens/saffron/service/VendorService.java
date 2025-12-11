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

    public VendorService(UserRepository userRepo, VendorRepository vendorRepo, PasswordEncoder passwordEncoder, AuditService auditService) {
        this.userRepo = userRepo;
        this.vendorRepo = vendorRepo;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
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
        return vendorRepo.findByCategory(category);
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
