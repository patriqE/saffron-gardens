-- V1: Initial schema for Saffron application
-- Users table

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    approved BOOLEAN DEFAULT false
);

-- Vendor table

CREATE TABLE IF NOT EXISTS vendor (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(150),
    business_name VARCHAR(255) NOT NULL,
    description TEXT,
    documents_path VARCHAR(1024),
    logo_path VARCHAR(1024),
    website VARCHAR(512),
    phone_number VARCHAR(50),
    email VARCHAR(255),
    address VARCHAR(512),
    city VARCHAR(150),
    state VARCHAR(150),

    CONSTRAINT fk_vendor_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_vendor_user
        UNIQUE (user_id)
);

CREATE INDEX IF NOT EXISTS idx_vendor_category
    ON vendor(category);

-- Audit events

CREATE TABLE IF NOT EXISTS audit_event (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(150) NOT NULL,
    action VARCHAR(150) NOT NULL,
    details TEXT,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL
        DEFAULT CURRENT_TIMESTAMP
);

-- Legacy SQL Server fragments preserved for history only
-- (NOT executed by Flyway)
-- ORIGINAL SQL SERVER FRAGMENTS BEGIN
-- ...
-- ORIGINAL SQL SERVER FRAGMENTS END
