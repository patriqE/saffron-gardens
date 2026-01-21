-- V16: Ensure super-admin has the intended password hash
-- This migration is idempotent: it updates the password_hash (and password column if present)
-- only when it's different or inserts the user if missing.

DO $$
BEGIN
    -- If user exists, update password_hash and password (if present)
    IF EXISTS (SELECT 1 FROM users WHERE email = 'saffrongardens2@gmail.com') THEN
        UPDATE users
        SET password_hash = '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW'
        WHERE email = 'saffrongardens2@gmail.com' AND password_hash IS DISTINCT FROM '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW';

        -- If a separate `password` column exists, keep it in sync
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='password') THEN
            UPDATE users
            SET password = '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW'
            WHERE email = 'saffrongardens2@gmail.com' AND (password IS DISTINCT FROM '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW' OR password IS NULL);
        END IF;
    ELSE
        -- Insert user (if missing) with required fields. If additional columns exist they'll be handled in later updates.
        INSERT INTO users (email, password_hash, role, is_active, created_at, approved, can_complete_profile)
        SELECT 'saffrongardens2@gmail.com',
               '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW',
               'SUPER_ADMIN', true, NOW(), true, false
        WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'saffrongardens2@gmail.com');

        -- If `password` column exists, update it as well
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='password') THEN
            UPDATE users
            SET password = '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW'
            WHERE email = 'saffrongardens2@gmail.com' AND (password IS DISTINCT FROM '$2a$10$AThMJ868.brtZW7UzqESnekYO157oBXOq6p6YeY9BZTKHOZWENwPW' OR password IS NULL);
        END IF;

        -- If `username` exists but is null, set it to local part
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='username') THEN
            UPDATE users
            SET username = split_part(email, '@', 1)
            WHERE email = 'saffrongardens2@gmail.com' AND (username IS NULL OR username = '');
        END IF;
    END IF;
END$$;
