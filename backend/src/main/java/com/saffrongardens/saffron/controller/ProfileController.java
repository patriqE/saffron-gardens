package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.entity.Planner;
import com.saffrongardens.saffron.entity.PortfolioFile;
import com.saffrongardens.saffron.entity.User;
import com.saffrongardens.saffron.entity.Vendor;
import com.saffrongardens.saffron.repository.PlannerRepository;
import com.saffrongardens.saffron.repository.PortfolioFileRepository;
import com.saffrongardens.saffron.repository.UserRepository;
import com.saffrongardens.saffron.repository.VendorRepository;
import com.saffrongardens.saffron.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepo;
    private final VendorRepository vendorRepo;
    private final PlannerRepository plannerRepo;
    private final PortfolioFileRepository fileRepo;
    private final AuditService auditService;

    public ProfileController(UserRepository userRepo, VendorRepository vendorRepo, PlannerRepository plannerRepo, PortfolioFileRepository fileRepo, AuditService auditService) {
        this.userRepo = userRepo;
        this.vendorRepo = vendorRepo;
        this.plannerRepo = plannerRepo;
        this.fileRepo = fileRepo;
        this.auditService = auditService;
    }

    @PostMapping(value = "/complete", consumes = {"multipart/form-data"})
    public ResponseEntity<?> completeProfile(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam Map<String, String> params,
                                             @RequestPart(value = "files", required = false) MultipartFile[] files) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthenticated"));
        String username = userDetails.getUsername();
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.isCanCompleteProfile()) return ResponseEntity.status(403).body(Map.of("error", "Not allowed to complete profile"));

        String role = user.getRole();
        List<String> savedFiles = new ArrayList<>();
        if (files != null && files.length > 0) {
            Path uploadRoot = Paths.get("uploads/portfolio/" + username);
            try {
                Files.createDirectories(uploadRoot);
                for (MultipartFile f : files) {
                    String filename = Instant.now().toEpochMilli() + "_" + f.getOriginalFilename();
                    Path dest = uploadRoot.resolve(filename);
                    f.transferTo(dest.toFile());
                    PortfolioFile pf = new PortfolioFile();
                    pf.setUser(user);
                    pf.setFilePath(dest.toString());
                    pf.setFileType(f.getContentType());
                    fileRepo.save(pf);
                    savedFiles.add(dest.toString());
                }
            } catch (IOException ex) {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to save files"));
            }
        }

        if ("VENDOR".equals(role)) {
            Vendor v = vendorRepo.findByUserUsername(username).orElseGet(() -> {
                Vendor nv = new Vendor();
                nv.setUser(user);
                return nv;
            });
            // populate vendor fields from params (if present)
            if (params.containsKey("businessName")) v.setBusinessName(params.get("businessName"));
            if (params.containsKey("category")) v.setCategory(params.get("category"));
            if (params.containsKey("description")) v.setDescription(params.get("description"));
            if (params.containsKey("website")) v.setWebsite(params.get("website"));
            if (params.containsKey("phoneNumber")) v.setPhoneNumber(params.get("phoneNumber"));
            if (params.containsKey("email")) v.setEmail(params.get("email"));
            if (params.containsKey("address")) v.setAddress(params.get("address"));
            if (params.containsKey("city")) v.setCity(params.get("city"));
            if (params.containsKey("state")) v.setState(params.get("state"));
            vendorRepo.save(v);
            auditService.record(username, "COMPLETE_VENDOR_PROFILE", "Completed vendor profile");
        } else if ("EVENT_PLANNER".equals(role)) {
            Planner p = plannerRepo.findByUserUsername(username).orElseGet(() -> {
                Planner np = new Planner();
                np.setUser(user);
                return np;
            });
            if (params.containsKey("fullName")) p.setFullName(params.get("fullName"));
            if (params.containsKey("companyName")) p.setCompanyName(params.get("companyName"));
            if (params.containsKey("description")) p.setDescription(params.get("description"));
            if (params.containsKey("website")) p.setWebsite(params.get("website"));
            if (params.containsKey("phoneNumber")) p.setPhoneNumber(params.get("phoneNumber"));
            if (params.containsKey("email")) p.setEmail(params.get("email"));
            plannerRepo.save(p);
            auditService.record(username, "COMPLETE_PLANNER_PROFILE", "Completed planner profile");
        } else {
            return ResponseEntity.status(400).body(Map.of("error", "Unsupported role for profile completion"));
        }

        // after successful completion, mark canCompleteProfile=false and approved=true (grant access)
        user.setCanCompleteProfile(false);
        user.setApproved(true);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("files", savedFiles, "message", "Profile completed"));
    }
}

