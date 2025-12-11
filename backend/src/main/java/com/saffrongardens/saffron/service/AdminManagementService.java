package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.UserRepository;
import com.saffrongardens.saffron.exception.OperationNotAllowedException;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Service
@Transactional
public class AdminManagementService {

    private static final Logger log = LoggerFactory.getLogger(AdminManagementService.class);

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;
    private final AuditService auditService;

    public AdminManagementService(UserRepository userRepo, PasswordEncoder passwordEncoder, Environment env, AuditService auditService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.auditService = auditService;
    }

    private String actorName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) return auth.getName();
        return "SYSTEM";
    }

    private String primaryIdentifier() {
        // Prefer username env var, fall back to email env var if present
        String u = env.getProperty("PRIMARY_SUPERADMIN_USERNAME");
        if (u != null && !u.isBlank()) return u.trim();
        String e = env.getProperty("PRIMARY_SUPERADMIN_EMAIL");
        return e == null ? "" : e.trim();
    }

    public boolean isPrimarySuperAdminUsername(String username) {
        String primary = primaryIdentifier();
        return primary != null && !primary.isEmpty() && primary.equalsIgnoreCase(username);
    }

    /**
     * Seed the primary super admin if environment variables are provided and the user does not already exist.
     */
    public void seedPrimarySuperAdminFromEnv() {
        String primary = primaryIdentifier();
        String rawPassword = env.getProperty("PRIMARY_SUPERADMIN_PASSWORD");
        if (primary == null || primary.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            log.debug("No primary super admin env provided; skipping seed");
            return; // nothing to do
        }

        Optional<User> existing = userRepo.findByUsername(primary);
        if (existing.isPresent()) {
            // ensure role is SUPER_ADMIN
            User u = existing.get();
            if (!"SUPER_ADMIN".equals(u.getRole())) {
                u.setRole("SUPER_ADMIN");
                u.setApproved(true);
                userRepo.save(u);
            }
            log.info("Primary super admin '{}' already exists; ensured SUPER_ADMIN role", primary);
            auditService.record(actorName(), "ENSURE_PRIMARY_SUPER_ADMIN", "Ensured role SUPER_ADMIN for " + primary);
            return;
        }

        // create new super admin
        User user = new User(primary, passwordEncoder.encode(rawPassword), "SUPER_ADMIN");
        user.setApproved(true);
        userRepo.save(user);
        log.info("Seeded primary super admin '{}'", primary);
        auditService.record(actorName(), "SEED_PRIMARY_SUPER_ADMIN", "Seeded primary super admin: " + primary);
    }

    /* Administrative operations with guarding against modifying the primary super admin */

    public User createAdmin(String username, String rawPassword) {
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot create an admin matching the protected primary super admin identifier");
        }
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists: " + username);
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), "ADMIN");
        user.setApproved(true);
        User savedAdmin = userRepo.save(user);
        log.info("Created ADMIN user '{}'", username);
        auditService.record(actorName(), "CREATE_ADMIN", "Created ADMIN user: " + username);
        return savedAdmin;
    }

    public User createSuperAdmin(String username, String rawPassword) {
        if (isPrimarySuperAdminUsername(username) && userRepo.findByUsername(username).isPresent()) {
            throw new OperationNotAllowedException("Cannot override the protected primary super admin");
        }
        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User already exists: " + username);
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), "SUPER_ADMIN");
        user.setApproved(true);
        User saved = userRepo.save(user);
        log.info("Created SUPER_ADMIN user '{}'", username);
        auditService.record(actorName(), "CREATE_SUPER_ADMIN", "Created SUPER_ADMIN user: " + username);
        return saved;
    }

    public void promoteToSuperAdmin(String username) {
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot modify the protected primary super admin via promote/demote APIs");
        }
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole("SUPER_ADMIN");
        userRepo.save(user);
        log.info("Promoted user '{}' to SUPER_ADMIN", username);
        auditService.record(actorName(), "PROMOTE_TO_SUPER_ADMIN", "Promoted user to SUPER_ADMIN: " + username);
    }

    public void demoteToAdmin(String username) {
        // Prevent demoting the protected primary super admin
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot demote the primary super admin");
        }
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole("ADMIN");
        userRepo.save(user);
        log.info("Demoted user '{}' to ADMIN", username);
        auditService.record(actorName(), "DEMOTE_TO_ADMIN", "Demoted user to ADMIN: " + username);
    }

    public void deleteAdminOrSuperAdmin(String username) {
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot delete the primary super admin");
        }
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepo.delete(user);
        log.info("Deleted user '{}'", username);
        auditService.record(actorName(), "DELETE_USER", "Deleted user: " + username);
    }

    /** Approve an existing user (e.g., EVENT_PLANNER) */
    public void approveUser(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.isApproved()) return; // idempotent
        user.setApproved(true);
        userRepo.save(user);
        log.info("Approved user id='{}' username='{}'", userId, user.getUsername());
        auditService.record(actorName(), "APPROVE_USER", "Approved user: " + user.getUsername());
    }
}
