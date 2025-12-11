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
    }
