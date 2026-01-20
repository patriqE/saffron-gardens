ALTER TABLE access_request
    ADD CONSTRAINT fk_access_request_reviewer
        FOREIGN KEY (reviewed_by)
            REFERENCES users(id)
            ON DELETE SET NULL;
