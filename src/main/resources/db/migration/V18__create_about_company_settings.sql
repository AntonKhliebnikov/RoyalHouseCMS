CREATE TABLE about_company_settings
(
    id                BIGINT PRIMARY KEY,
    banner_image_path VARCHAR(500),
    banner_text       VARCHAR(255),
    title             VARCHAR(255) NOT NULL DEFAULT '',
    description       TEXT,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_about_company_settings_single_row CHECK (id = 1)
);

INSERT INTO about_company_settings (id, title)
VALUES (1, 'О компании')
ON CONFLICT (id) DO NOTHING;

CREATE TRIGGER trg_about_company_settings_set_updated_at
    BEFORE UPDATE
    ON about_company_settings
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();