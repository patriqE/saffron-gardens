ALTER TABLE gallery_item
    ADD COLUMN published BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE gallery_item
    ADD COLUMN uploaded_by BIGINT;

ALTER TABLE gallery_item
    ADD CONSTRAINT fk_gallery_uploaded_by
        FOREIGN KEY (uploaded_by) REFERENCES users(id);

ALTER TABLE gallery_item
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE gallery_item
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
