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
