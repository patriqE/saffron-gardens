package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.dto.VendorApplicationDTO;
import com.saffrongardens.saffron.entity.Vendor;
import com.saffrongardens.saffron.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @RequestMapping("/apply")
    public String apply(@RequestBody VendorApplicationDTO dto){
        vendorService.apply(dto);
        return "Vendor application submitted... Wait for admin approval.";
    }

    @GetMapping("/by-category")
    public List<Vendor> byCategory(@RequestParam String category){
        return vendorService.getVendorsByCategory(category);
    }

    @GetMapping("/profile")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('VENDOR')")
    public com.saffrongardens.saffron.controller.dto.VendorProfileDTO myProfile(org.springframework.security.core.Authentication auth) {
        com.saffrongardens.saffron.entity.Vendor v = vendorService.getVendorForUser(auth.getName());
        if (v == null) return null;
        com.saffrongardens.saffron.controller.dto.VendorProfileDTO dto = new com.saffrongardens.saffron.controller.dto.VendorProfileDTO();
        dto.setBusinessName(v.getBusinessName());
        dto.setCategory(v.getCategory());
        dto.setDescription(v.getDescription());
        dto.setWebsite(v.getWebsite());
        dto.setPhoneNumber(v.getPhoneNumber());
        dto.setEmail(v.getEmail());
        dto.setAddress(v.getAddress());
        dto.setCity(v.getCity());
        dto.setState(v.getState());
        dto.setLogoPath(v.getLogoPath());
        dto.setDocumentsPath(v.getDocumentsPath());
        return dto;
    }

    @PutMapping("/profile")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('VENDOR')")
    public com.saffrongardens.saffron.controller.dto.VendorProfileDTO updateProfile(org.springframework.security.core.Authentication auth, @RequestBody com.saffrongardens.saffron.controller.dto.VendorProfileDTO updated) {
        com.saffrongardens.saffron.entity.Vendor v = vendorService.updateVendorProfile(auth.getName(), updated);
        com.saffrongardens.saffron.controller.dto.VendorProfileDTO dto = new com.saffrongardens.saffron.controller.dto.VendorProfileDTO();
        dto.setBusinessName(v.getBusinessName());
        dto.setCategory(v.getCategory());
        dto.setDescription(v.getDescription());
        dto.setWebsite(v.getWebsite());
        dto.setPhoneNumber(v.getPhoneNumber());
        dto.setEmail(v.getEmail());
        dto.setAddress(v.getAddress());
        dto.setCity(v.getCity());
        dto.setState(v.getState());
        dto.setLogoPath(v.getLogoPath());
        dto.setDocumentsPath(v.getDocumentsPath());
        return dto;
    }

    @GetMapping("/list")
    public List<Vendor> list(@RequestParam(required = false) String category) {
        return vendorService.listApprovedVendors(category);
    }

    @GetMapping("/{id}/availability")
    public org.springframework.http.ResponseEntity<?> availability(@PathVariable Long id, @RequestParam String start, @RequestParam String end) {
        // vendor id passed is vendor.user.id or vendor id? we accept vendor user id
        try {
            java.time.LocalDateTime s = java.time.LocalDateTime.parse(start);
            java.time.LocalDateTime e = java.time.LocalDateTime.parse(end);
            // find vendor by id -> Vendor entity
            Vendor v = vendorService.findAll().stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (v == null) return org.springframework.http.ResponseEntity.notFound().build();
            Long vendorUserId = v.getUser().getId();
            boolean ok = vendorService.isVendorAvailable(vendorUserId, s, e);
            return org.springframework.http.ResponseEntity.ok(java.util.Map.of("available", ok));
        } catch (Exception ex) {
            return org.springframework.http.ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    }
