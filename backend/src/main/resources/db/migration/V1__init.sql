-- Initial schema for Saffron application
-- Users table
CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50),
  approved BOOLEAN DEFAULT false
);

-- Vendor table
CREATE TABLE IF NOT EXISTS vendor (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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
  state VARCHAR(150)
);

-- Audit events
CREATE TABLE IF NOT EXISTS audit_event (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(150) NOT NULL,
  action VARCHAR(150) NOT NULL,
  details TEXT,
  timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);
-- V1__init.sql - initial schema for users and vendor

CREATE TABLE users (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50),
  approved BIT DEFAULT 0
);

CREATE TABLE vendor (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  user_id BIGINT NOT NULL,
  category VARCHAR(100),
  business_name VARCHAR(255) NOT NULL,
  description VARCHAR(MAX),
  email VARCHAR(255),
  documents_path VARCHAR(255),
  logo_path VARCHAR(255),
  website VARCHAR(255),
  phone_number VARCHAR(50),
  address VARCHAR(255),
  city VARCHAR(100),
  state VARCHAR(100),
  CONSTRAINT fk_vendor_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_vendor_category ON vendor(category);
