-- V14: Ensure `approved` and `can_complete_profile` columns exist (idempotent), fix defaults/backfill, and insert a super admin user if missing

-- Add `approved` column only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'approved'
    ) THEN
        ALTER TABLE users
            ADD COLUMN approved BOOLEAN;
    END IF;

    -- Ensure column has a default (set to true)
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'approved' AND column_default IS NOT NULL
    ) THEN
        ALTER TABLE users ALTER COLUMN approved SET DEFAULT true;
    END IF;

    -- Backfill any existing NULLs to a safe value
    UPDATE users SET approved = true WHERE approved IS NULL;

    -- Make the column NOT NULL to match entity expectations
    ALTER TABLE users ALTER COLUMN approved SET NOT NULL;

    -- Ensure `can_complete_profile` column exists and is safe (default false)
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'can_complete_profile'
    ) THEN
        ALTER TABLE users
            ADD COLUMN can_complete_profile BOOLEAN;
    END IF;

    -- Ensure default for can_complete_profile is false
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'can_complete_profile' AND column_default IS NOT NULL
    ) THEN
        ALTER TABLE users ALTER COLUMN can_complete_profile SET DEFAULT false;
    END IF;

    -- Backfill NULLs to false
    UPDATE users SET can_complete_profile = false WHERE can_complete_profile IS NULL;

    -- Make the column NOT NULL
    ALTER TABLE users ALTER COLUMN can_complete_profile SET NOT NULL;
END$$;

-- Insert super admin if not exists. Build the INSERT dynamically to handle different schema variants
DO $$
DECLARE
    cols TEXT := 'email, password_hash, role, is_active, created_at, approved, can_complete_profile';
    vals TEXT := quote_literal('saffrongardens2@gmail.com') || ', ' || quote_literal('$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW') || ', ' || quote_literal('SUPER_ADMIN') || ', true, NOW(), true, false';
    insertSql TEXT;
BEGIN
    -- If `password` column exists, also insert into it (use same hash)
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='password') THEN
        cols := cols || ', password';
        vals := vals || ', ' || quote_literal('$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW');
    END IF;

    -- If `username` column exists, insert a sensible default username (ensure uniqueness by appending random suffix if necessary)
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='username') THEN
        -- prefer a deterministic username based on email local-part
        cols := cols || ', username';
        vals := vals || ', ' || quote_literal(split_part('saffrongardens2@gmail.com', '@', 1));
    END IF;

    insertSql := format('INSERT INTO users (%s) SELECT %s WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = %L)', cols, vals, 'saffrongardens2@gmail.com');
    EXECUTE insertSql;
END$$;
