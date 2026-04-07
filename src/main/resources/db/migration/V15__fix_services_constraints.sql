ALTER TABLE services
    DROP CONSTRAINT IF EXISTS chk_services_preview_image_path_not_blank;

ALTER TABLE services
    ADD CONSTRAINT chk_services_preview_image_path_not_blank
        CHECK (preview_image_path IS NULL OR btrim(preview_image_path) <> '');

ALTER TABLE services
    ALTER COLUMN is_visible SET DEFAULT TRUE;

UPDATE services
SET is_visible = TRUE
WHERE is_visible IS NULL;

ALTER TABLE services
    ALTER COLUMN is_visible SET NOT NULL;