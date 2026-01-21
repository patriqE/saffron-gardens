-- V15: Enforce that can_complete_profile can only be true for VENDOR or PLANNER roles

DO $$
BEGIN
    -- create constraint only if it doesn't already exist
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_can_complete_role'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT chk_can_complete_role CHECK (NOT can_complete_profile OR role IN ('VENDOR','PLANNER'));
    END IF;
END$$;
