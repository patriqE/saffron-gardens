package com.saffrongardens.saffron.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saffrongardens.saffron.controller.dto.VendorProfileDTO;
import com.saffrongardens.saffron.dto.VendorApplicationDTO;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.entity.Vendor;
import com.saffrongardens.saffron.service.VendorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendorController.class)
@Import(TestSecurityConfig.class) // minimal security config for @PreAuthorize
class VendorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @Autowired
    private ObjectMapper objectMapper;

    /* ----------------------------------
       APPLY FOR VENDOR
       ---------------------------------- */
    @Test
    void apply_submitsVendorApplication() throws Exception {
        VendorApplicationDTO dto = new VendorApplicationDTO();
        dto.setUsername("vendor1");
        dto.setPassword("password");
        dto.setBusinessName("Elite DJ");
        dto.setCategory("DJ");
        dto.setDescription("Professional DJ");
        dto.setEmail("dj@test.com");

        doNothing().when(vendorService).apply(any(VendorApplicationDTO.class));

        mockMvc.perform(post("/api/vendor/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(content().string("Vendor application submitted... Wait for admin approval."));
    }

    /* ----------------------------------
       LIST BY CATEGORY
       ---------------------------------- */
    @Test
    void byCategory_returnsVendors() throws Exception {
        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setBusinessName("Elite DJ");
        vendor.setCategory("DJ");

        when(vendorService.getVendorsByCategory("DJ"))
                .thenReturn(List.of(vendor));

        mockMvc.perform(get("/api/vendor/by-category")
                .param("category", "DJ"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    /* ----------------------------------
       MY PROFILE (SECURED)
       ---------------------------------- */
    @Test
    @WithMockUser(username = "vendor1", roles = "VENDOR")
    void myProfile_returnsVendorProfile() throws Exception {
        Vendor vendor = new Vendor();
        vendor.setBusinessName("Elite DJ");
        vendor.setCategory("DJ");
        vendor.setEmail("dj@test.com");

        when(vendorService.getVendorForUser("vendor1"))
                .thenReturn(vendor);

        mockMvc.perform(get("/api/vendor/profile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessName").value("Elite DJ"))
            .andExpect(jsonPath("$.category").value("DJ"))
            .andExpect(jsonPath("$.email").value("dj@test.com"));
    }

    /* ----------------------------------
       UPDATE PROFILE (SECURED)
       ---------------------------------- */
    @Test
    @WithMockUser(username = "vendor1", roles = "VENDOR")
    void updateProfile_updatesVendorProfile() throws Exception {
        VendorProfileDTO updated = new VendorProfileDTO();
        updated.setBusinessName("New Name");
        updated.setCategory("Catering");

        Vendor saved = new Vendor();
        saved.setBusinessName("New Name");
        saved.setCategory("Catering");

        when(vendorService.updateVendorProfile(eq("vendor1"), any(VendorProfileDTO.class)))
                .thenReturn(saved);

        mockMvc.perform(put("/api/vendor/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessName").value("New Name"))
            .andExpect(jsonPath("$.category").value("Catering"));
    }

    /* ----------------------------------
       LIST APPROVED VENDORS
       ---------------------------------- */
    @Test
    void list_returnsApprovedVendors() throws Exception {
        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setBusinessName("Approved Vendor");

        when(vendorService.listApprovedVendors(null))
                .thenReturn(List.of(vendor));

        mockMvc.perform(get("/api/vendor/list"))
            .andExpect(status().isOk());
    }

    /* ----------------------------------
       AVAILABILITY
       ---------------------------------- */
    @Test
    void availability_returnsAvailableTrue() throws Exception {
        User user = new User();
        user.setId(10L);

        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setUser(user);

        when(vendorService.findAll())
                .thenReturn(List.of(vendor));

        when(vendorService.isVendorAvailable(
                eq(10L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(true);

        mockMvc.perform(get("/api/vendor/1/availability")
                .param("start", LocalDateTime.now().toString())
                .param("end", LocalDateTime.now().plusHours(2).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available").value(true));
    }
}
