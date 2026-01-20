-- V12: Add version column for optimistic locking to access_request

ALTER TABLE access_request
    ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0 NOT NULL;
