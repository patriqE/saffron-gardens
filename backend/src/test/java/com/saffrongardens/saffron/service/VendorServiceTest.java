package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.VendorAssignment;
import com.saffrongardens.saffron.repository.VendorAssignmentRepository;
import com.saffrongardens.saffron.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorServiceTest {

    private VendorRepository vendorRepo;
    private VendorAssignmentRepository vendorAssignmentRepo;
    private VendorService vendorService;

    @BeforeEach
    void setup() {
        vendorRepo = mock(VendorRepository.class);
        vendorAssignmentRepo = mock(VendorAssignmentRepository.class);
        vendorService = new VendorService(null, vendorRepo, null, null, vendorAssignmentRepo);
    }

    @Test
    void isVendorAvailable_noConflicts_returnsTrue() {
        when(vendorAssignmentRepo.findConflictingForVendor(anyLong(), any(), any(), any())).thenReturn(Collections.emptyList());
        boolean ok = vendorService.isVendorAvailable(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        assertTrue(ok);
    }

    @Test
    void isVendorAvailable_withConflicts_returnsFalse() {
        VendorAssignment a = new VendorAssignment();
        when(vendorAssignmentRepo.findConflictingForVendor(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(a));
        boolean ok = vendorService.isVendorAvailable(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        assertFalse(ok);
    }
}
