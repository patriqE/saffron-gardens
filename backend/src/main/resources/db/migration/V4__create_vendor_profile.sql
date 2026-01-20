CREATE TABLE vendor_profile (
            id BIGSERIAL PRIMARY KEY,

            user_id BIGINT NOT NULL,

            business_name VARCHAR(255) NOT NULL,
            business_description TEXT,

            phone_number VARCHAR(50),
            address TEXT,

            verified BOOLEAN NOT NULL DEFAULT false,

            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

            CONSTRAINT uq_vendor_user UNIQUE (user_id),

            CONSTRAINT fk_vendor_user
                FOREIGN KEY (user_id)
                REFERENCES users(id)
                   ON DELETE CASCADE
);
