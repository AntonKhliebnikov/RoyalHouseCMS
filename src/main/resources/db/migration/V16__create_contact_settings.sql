CREATE TABLE IF NOT EXISTS contact_settings
(
    id                BIGINT PRIMARY KEY,
    phone             VARCHAR(32),
    viber_phone       VARCHAR(32),
    telegram_username VARCHAR(64),
    email             VARCHAR(255),
    instagram_url     VARCHAR(255),
    facebook_url      VARCHAR(255),
    address           VARCHAR(255),
    created_at        timestamptz NOT NULL DEFAULT now(),
    updated_at        timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT chk_contact_settings_singleton
        CHECK (id = 1),

    CONSTRAINT chk_contact_settings_phone_not_blank
        CHECK (phone IS NULL OR btrim(phone) <> ''),

    CONSTRAINT chk_contact_settings_viber_phone_not_blank
        CHECK (viber_phone IS NULL OR btrim(viber_phone) <> ''),

    CONSTRAINT chk_contact_settings_telegram_username_not_blank
        CHECK (telegram_username IS NULL OR btrim(telegram_username) <> ''),

    CONSTRAINT chk_contact_settings_email_not_blank
        CHECK (email IS NULL OR btrim(email) <> ''),

    CONSTRAINT chk_contact_settings_instagram_url_not_blank
        CHECK (instagram_url IS NULL OR btrim(instagram_url) <> ''),

    CONSTRAINT chk_contact_settings_facebook_url_not_blank
        CHECK (facebook_url IS NULL OR btrim(facebook_url) <> ''),

    CONSTRAINT chk_contact_settings_address_not_blank
        CHECK (address IS NULL OR btrim(address) <> '')
);

INSERT INTO contact_settings (id,
                              phone,
                              viber_phone,
                              telegram_username,
                              email,
                              instagram_url,
                              facebook_url,
                              address)
VALUES (1,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL)
ON CONFLICT (id) DO NOTHING;

DROP TRIGGER IF EXISTS trg_contact_settings_set_updated_at ON contact_settings;

CREATE TRIGGER trg_contact_settings_set_updated_at
    BEFORE UPDATE
    ON contact_settings
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();