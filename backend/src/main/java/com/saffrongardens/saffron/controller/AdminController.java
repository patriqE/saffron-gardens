package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/vendor/approve/{vendorId}")
    public String approveVendor(@PathVariable Long vendorId) {
        vendorService.approveVendor(vendorId);
        return "Vendor approved successfully";
    }
}
