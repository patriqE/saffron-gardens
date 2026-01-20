CREATE TABLE users (
            id BIGSERIAL PRIMARY KEY,

            email VARCHAR(255) NOT NULL,
            password_hash VARCHAR(255) NOT NULL,

                role VARCHAR(30) NOT NULL
                CHECK (role IN ('ADMIN', 'SUPER_ADMIN', 'VENDOR', 'PLANNER')),

                    is_active BOOLEAN NOT NULL DEFAULT true,

                    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uq_users_email UNIQUE (email)
);
