-- V11: Add rejection_reason column to access_request to match AccessRequest entity

ALTER TABLE access_request
    ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
