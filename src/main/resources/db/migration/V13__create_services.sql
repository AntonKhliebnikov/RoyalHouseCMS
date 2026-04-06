CREATE TABLE IF NOT EXISTS services
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    description        TEXT         NOT NULL,
    banner_image_path  VARCHAR(500),
    preview_image_path VARCHAR(500),
    createdAt          timestamptz  NOT NULL DEFAULT now(),
    updatedAt          timestamptz  NOT NULL DEFAULT now(),
    is_visible         BOOLEAN               DEFAULT TRUE,

    CONSTRAINT chk_services_name_not_blank
        CHECK (btrim(name) <> ''),

    CONSTRAINT chk_services_description_not_blank
        CHECK ( btrim(description) <> ''),

    CONSTRAINT chk_services_banner_image_path_not_blank
        CHECK (banner_image_path IS NULL OR btrim(banner_image_path) <> ''),

    CONSTRAINT chk_services_preview_image_path_not_blank
        CHECK (banner_image_path IS NULL OR btrim(preview_image_path) <> '')
);

CREATE TRIGGER trg_services_set_updated_at
    BEFORE UPDATE
    ON services
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();