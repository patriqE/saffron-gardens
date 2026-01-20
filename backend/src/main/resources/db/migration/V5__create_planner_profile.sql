CREATE TABLE planner_profile (
            id BIGSERIAL PRIMARY KEY,

            user_id BIGINT NOT NULL,

            full_name VARCHAR(255) NOT NULL,
            phone_number VARCHAR(50),

            years_of_experience INT,
            portfolio_url VARCHAR(255),

                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

            CONSTRAINT uq_planner_user UNIQUE (user_id),

                CONSTRAINT fk_planner_user
                    FOREIGN KEY (user_id)
                        REFERENCES users(id)
                            ON DELETE CASCADE
);
