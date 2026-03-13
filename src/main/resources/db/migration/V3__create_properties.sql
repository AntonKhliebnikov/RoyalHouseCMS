-- 1) Таблица объектов недвижимости (properties)
CREATE TABLE IF NOT EXISTS properties
(
    id            BIGSERIAL PRIMARY KEY,
    property_type VARCHAR(20)    NOT NULL,
    area          NUMERIC(10, 2) NOT NULL,
    price         NUMERIC(15, 2) NOT NULL,
    rooms         INTEGER,
    floor         INTEGER,
    total_floors  INTEGER,
    created_at    timestamptz    NOT NULL DEFAULT now(),
    updated_at    timestamptz    NOT NULL DEFAULT now(),

    CONSTRAINT chk_properties_type CHECK ( property_type IN ('APARTMENT', 'HOUSE', 'COMMERCIAL', 'LAND')),
    CONSTRAINT chk_properties_area CHECK (area > 0),
    CONSTRAINT chk_properties_price CHECK (price >= 0),
    CONSTRAINT chk_properties_rooms CHECK (rooms IS NULL OR rooms >= 0),
    CONSTRAINT chk_properties_floor CHECK (floor IS NULL OR floor >= 0),
    CONSTRAINT chk_properties_total_floors CHECK (total_floors IS NULL OR total_floors >= 0),
    CONSTRAINT chk_properties_floor_not_greater_than_total
        CHECK (floor IS NULL
            OR total_floors IS NULL
            OR floor <= total_floors
            )
);

CREATE INDEX IF NOT EXISTS inx_properties_created_at ON properties (created_at);
CREATE INDEX IF NOT EXISTS inx_properties_type ON properties (property_type);
CREATE INDEX IF NOT EXISTS inx_properties_area ON properties (area);
CREATE INDEX IF NOT EXISTS inx_properties_price ON properties (price);

-- 2) updated_at: триггер для авто-обновления при UPDATE
-- (PostgreSQL сам updated_at не обновляет — это делаем триггером)

DROP TRIGGER IF EXISTS trg_properties_set_updated_at ON properties;

CREATE TRIGGER trg_properties_set_updated_at
    BEFORE UPDATE
    ON properties
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();