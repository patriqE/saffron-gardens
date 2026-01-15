-- Add email and role-specific columns to registration_requests and email to users
ALTER TABLE registration_requests
    ADD COLUMN email VARCHAR(255);

ALTER TABLE registration_requests
    ADD COLUMN business_name VARCHAR(255);

ALTER TABLE registration_requests
    ADD COLUMN website VARCHAR(512);

ALTER TABLE registration_requests
    ADD COLUMN full_name VARCHAR(255);

ALTER TABLE registration_requests
    ADD COLUMN other_socials VARCHAR(512);

-- Add email to users table
ALTER TABLE users
    ADD COLUMN email VARCHAR(255);

-- Optional: create index on registration_requests.email
CREATE INDEX IF NOT EXISTS idx_registration_requests_email ON registration_requests(email);

