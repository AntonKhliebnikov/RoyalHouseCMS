-- 1) Таблица заявок (applications)
CREATE TABLE IF NOT EXISTS applications
(
    id         BIGSERIAL PRIMARY KEY,
    full_name  VARCHAR(255) NOT NULL,
    phone      VARCHAR(32)  NOT NULL,
    email      VARCHAR(255),
    comment    TEXT,
    status     VARCHAR(16)  NOT NULL DEFAULT 'NEW',
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz  NOT NULL DEFAULT now(),

    -- Защита от пустых строк (когда "не null", но реально пусто/пробелы)
    CONSTRAINT chk_applications_full_name_not_blank CHECK ( btrim(full_name) <> '' ),
    CONSTRAINT chk_applications_phone_not_blank CHECK ( btrim(phone) <> '' ),
    CONSTRAINT chk_applications_email_not_blank CHECK ( email IS NULL OR btrim(email) <> '' ),

    -- Допускаем только 2 статуса: NEW и ANSWERED
    CONSTRAINT chk_applications_status CHECK ( status IN ('NEW', 'ANSWERED'))
);

-- Индексы под админский список + фильтры
CREATE INDEX IF NOT EXISTS ind_applications_created_at ON applications (created_at DESC);
CREATE INDEX IF NOT EXISTS ind_applications_status ON applications (status);
CREATE INDEX IF NOT EXISTS ind_applications_phone ON applications (phone);
CREATE INDEX IF NOT EXISTS ind_applications_email ON applications (email);

-- 2) Таблица получателей заявок (application_recipient_emails)
CREATE TABLE IF NOT EXISTS application_recipient_emails
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT chk_app_recipient_email_not_blank CHECK ( btrim(email) <> '' )
);

-- Уникальность email без учета регистра (Test@x.com и test@x.com считаем одинаковыми)
CREATE UNIQUE INDEX IF NOT EXISTS ux_app_recipient_email_lower
    ON application_recipient_emails (lower(email));

-- Индекс на активность (быстро выбирать только активные)
CREATE INDEX IF NOT EXISTS ind_app_recipient_email_is_active
    ON application_recipient_emails (is_active);


-- 3) updated_at: триггер для авто-обновления при UPDATE
-- (PostgreSQL сам updated_at не обновляет — это делаем триггером)
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_applications_set_updated_at ON applications;

CREATE TRIGGER trg_applications_set_updated_at
    BEFORE UPDATE
    ON applications
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();