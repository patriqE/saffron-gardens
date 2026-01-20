-- V1: Create request table for request-access (vendors and planners)

CREATE TABLE IF NOT EXISTS access_request (
          id BIGSERIAL PRIMARY KEY,

          email VARCHAR(255) NOT NULL UNIQUE,
          role VARCHAR(50) NOT NULL, -- VENDOR or PLANNER

-- Vendor-specific
    business_name VARCHAR(255),
    website VARCHAR(512),

    -- Planner-specific
    full_name VARCHAR(255),
    other_socials TEXT,

    -- Common
    instagram_profile VARCHAR(255) NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    -- PENDING | APPROVED | REJECTED

    reviewed_by BIGINT,
    reviewed_at TIMESTAMP WITH TIME ZONE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

            CONSTRAINT chk_access_request_role
                CHECK (role IN ('VENDOR', 'PLANNER')),

    CONSTRAINT chk_access_request_status
    CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
    );

CREATE INDEX IF NOT EXISTS idx_access_request_status
    ON access_request(status);

CREATE INDEX IF NOT EXISTS idx_access_request_role
    ON access_request(role);

