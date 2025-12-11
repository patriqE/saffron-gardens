package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.exception.OperationNotAllowedException;
import com.saffrongardens.saffron.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminManagementServiceTest {

    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private Environment env;
    private AuditService auditService;
    private AdminManagementService service;

    @BeforeEach
    void setup() {
        userRepo = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        env = mock(Environment.class);
        auditService = mock(AuditService.class);
        service = new AdminManagementService(userRepo, passwordEncoder, env, auditService);
    }

    @Test
    void seedPrimary_createsUserWhenMissing() {
        when(env.getProperty("PRIMARY_SUPERADMIN_USERNAME")).thenReturn("primaryUser");
        when(env.getProperty("PRIMARY_SUPERADMIN_PASSWORD")).thenReturn("secret");
        when(userRepo.findByUsername("primaryUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        service.seedPrimarySuperAdminFromEnv();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo, times(1)).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("primaryUser", saved.getUsername());
        assertEquals("encoded", saved.getPassword());
        assertEquals("SUPER_ADMIN", saved.getRole());
        assertTrue(saved.isApproved());
    }

    @Test
    void createAdmin_forbiddenWhenMatchesPrimary() {
        when(env.getProperty("PRIMARY_SUPERADMIN_USERNAME")).thenReturn("protected");
        // call under test
        assertThrows(OperationNotAllowedException.class, () -> service.createAdmin("protected", "pw"));
    }
}
