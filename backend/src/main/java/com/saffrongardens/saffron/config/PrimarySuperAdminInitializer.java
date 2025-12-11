package com.saffrongardens.saffron.config;

import com.saffrongardens.saffron.service.AdminManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrimarySuperAdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PrimarySuperAdminInitializer.class);

    private final AdminManagementService adminManagementService;

    public PrimarySuperAdminInitializer(AdminManagementService adminManagementService) {
        this.adminManagementService = adminManagementService;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            adminManagementService.seedPrimarySuperAdminFromEnv();
            logger.info("Primary super admin seeding attempted (if env vars were provided).");
        } catch (Exception ex) {
            logger.error("Failed to seed primary super admin", ex);
        }
    }
}
