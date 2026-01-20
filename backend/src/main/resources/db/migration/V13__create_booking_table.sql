-- V13: Create booking table

CREATE TABLE IF NOT EXISTS booking (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT,
    planner_user_id BIGINT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_amount NUMERIC(12,2),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_booking_hall_id ON booking(hall_id);
CREATE INDEX IF NOT EXISTS idx_booking_planner_user_id ON booking(planner_user_id);

-- Add foreign keys if the referenced tables and constraints do not already exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints tc
        WHERE tc.constraint_name = 'fk_booking_hall'
    ) THEN
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'hall') THEN
            ALTER TABLE booking
                ADD CONSTRAINT fk_booking_hall FOREIGN KEY (hall_id) REFERENCES hall(id) ON DELETE SET NULL;
        END IF;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints tc
        WHERE tc.constraint_name = 'fk_booking_planner'
    ) THEN
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
            ALTER TABLE booking
                ADD CONSTRAINT fk_booking_planner FOREIGN KEY (planner_user_id) REFERENCES users(id) ON DELETE SET NULL;
        END IF;
    END IF;
END$$;
