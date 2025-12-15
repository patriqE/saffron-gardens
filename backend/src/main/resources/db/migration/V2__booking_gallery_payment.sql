-- V2: Booking, halls, vendor assignments, payment records, gallery, content pages
-- Postgres + H2 compatible

CREATE TABLE IF NOT EXISTS hall (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    capacity INTEGER,
    location VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS booking (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL,
    planner_user_id BIGINT NOT NULL,

    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,

    total_amount NUMERIC(12,2),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_booking_hall
        FOREIGN KEY (hall_id)
        REFERENCES hall(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_booking_planner
        FOREIGN KEY (planner_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_booking_hall_time
    ON booking(hall_id, start_time, end_time);

CREATE TABLE IF NOT EXISTS vendor_assignment (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    vendor_user_id BIGINT,
    vendor_type VARCHAR(100),
    notes TEXT,
    price NUMERIC(12,2),

    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_assignment_booking
        FOREIGN KEY (booking_id)
        REFERENCES booking(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_assignment_vendor
        FOREIGN KEY (vendor_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_vendor_assignment_booking
    ON vendor_assignment(booking_id);

CREATE TABLE IF NOT EXISTS payment_record (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT,
    amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'NGN',
    gateway VARCHAR(50),
    gateway_transaction VARCHAR(255),
    status VARCHAR(100),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id)
        REFERENCES booking(id)
        ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_payment_booking
    ON payment_record(booking_id);

CREATE TABLE IF NOT EXISTS gallery_item (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(512) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    path VARCHAR(1024),

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS content_page (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255),
    body TEXT,
    published BOOLEAN DEFAULT false,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE
);

-- End of V2
