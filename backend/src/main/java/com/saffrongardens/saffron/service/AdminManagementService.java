package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.UserRepository;
import com.saffrongardens.saffron.exception.OperationNotAllowedException;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminManagementService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public AdminManagementService(UserRepository userRepo, PasswordEncoder passwordEncoder, Environment env) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
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
            return;
        }

        // create new super admin
        User user = new User(primary, passwordEncoder.encode(rawPassword), "SUPER_ADMIN");
        user.setApproved(true);
        userRepo.save(user);
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
        return userRepo.save(user);
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
        return userRepo.save(user);
    }

    public void promoteToSuperAdmin(String username) {
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot modify the protected primary super admin via promote/demote APIs");
        }
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole("SUPER_ADMIN");
        userRepo.save(user);
    }

    public void demoteToAdmin(String username) {
        // Prevent demoting the protected primary super admin
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot demote the primary super admin");
        }
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole("ADMIN");
        userRepo.save(user);
    }

    public void deleteAdminOrSuperAdmin(String username) {
        if (isPrimarySuperAdminUsername(username)) {
            throw new OperationNotAllowedException("Cannot delete the primary super admin");
        }
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepo.delete(user);
    }
}
