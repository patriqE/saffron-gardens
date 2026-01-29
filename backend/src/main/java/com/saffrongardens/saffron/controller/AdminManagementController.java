package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.AdminManagementService;
import com.saffrongardens.saffron.controller.dto.UserDTO;
import com.saffrongardens.saffron.controller.dto.CreateAdminRequest;
import com.saffrongardens.saffron.controller.dto.UsernameRequest;
import com.saffrongardens.saffron.controller.dto.UserListDTO;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins")
public class AdminManagementController {

    private final AdminManagementService adminService;
    private final UserRepository userRepository;

    public AdminManagementController(AdminManagementService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    // Create a normal admin
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest req) {
        var created = adminService.createAdmin(req.getUsername(), req.getPassword());
        UserDTO dto = new UserDTO(created.getId(), created.getUsername(), created.getRole(), created.isApproved());
        return ResponseEntity.ok(dto);
    }

    // Create a super admin
    @PostMapping("/create-super")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createSuperAdmin(@Valid @RequestBody CreateAdminRequest req) {
        var created = adminService.createSuperAdmin(req.getUsername(), req.getPassword());
        UserDTO dto = new UserDTO(created.getId(), created.getUsername(), created.getRole(), created.isApproved());
        return ResponseEntity.ok(dto);
    }

    // Promote existing user to super admin
    @PostMapping("/promote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> promote(@Valid @RequestBody UsernameRequest req) {
        adminService.promoteToSuperAdmin(req.getUsername());
        return ResponseEntity.ok().build();
    }

    // Demote a super admin to admin
    @PostMapping("/demote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> demote(@Valid @RequestBody UsernameRequest req) {
        adminService.demoteToAdmin(req.getUsername());
        return ResponseEntity.ok().build();
    }

    // Delete admin or super admin
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable String username) {
        adminService.deleteAdminOrSuperAdmin(username);
        return ResponseEntity.noContent().build();
    }

    // New: paged list registered users for admins/super-admins
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Page<UserListDTO>> listUsers(
            Authentication authentication,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "approved", required = false) Boolean approved,
            @RequestParam(name = "q", required = false) String q,
            Pageable pageable
    ) {
        boolean callerIsSuper = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        Specification<User> spec = Specification.where(null);

        if (role != null && !role.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.upper(root.get("role")), role.toUpperCase()));
        }

        if (approved != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("approved"), approved));
        }

        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("email")), like)
            ));
        }

        // If caller is ADMIN, exclude SUPER_ADMINs
        if (!callerIsSuper) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.isNull(root.get("role")),
                    cb.notEqual(cb.upper(root.get("role")), "SUPER_ADMIN")
            ));
        }

        Page<User> page = userRepository.findAll(spec, pageable);
        Page<UserListDTO> dtoPage = page.map(u -> new UserListDTO(u.getId(), u.getUsername(), u.getEmail(), u.getRole(), u.isApproved()));
        return ResponseEntity.ok(dtoPage);
    }
}
