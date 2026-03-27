CREATE TABLE new_buildings
(
    id                         BIGSERIAL PRIMARY KEY,
    name                       VARCHAR(255) NOT NULL,
    banner_image_path          VARCHAR(500),
    sort_order                 INTEGER      NOT NULL DEFAULT 0,
    is_active                  BOOLEAN      NOT NULL DEFAULT TRUE,

    city                       VARCHAR(120),
    district                   VARCHAR(120),
    street                     VARCHAR(150),
    house_number               VARCHAR(30),
    latitude                   NUMERIC(9, 6),
    longitude                  NUMERIC(9, 6),

    about_description          TEXT,
    location_description       TEXT,
    infrastructure_description TEXT,
    apartments_description     TEXT,

    panorama_image_path        VARCHAR(500),

    created_at                 TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_new_buildings_name_not_blank
        CHECK (btrim(name) <> ''),

    CONSTRAINT chk_new_buildings_sort_order
        CHECK (sort_order > 0),

    CONSTRAINT chk_new_buildings_city_not_blank
        CHECK (city IS NULL OR btrim(city) <> ''),

    CONSTRAINT chk_new_buildings_district_not_blank
        CHECK (district IS NULL OR btrim(district) <> ''),

    CONSTRAINT chk_new_buildings_street_not_blank
        CHECK (street IS NULL OR btrim(street) <> ''),

    CONSTRAINT chk_new_buildings_house_number_not_blank
        CHECK (house_number IS NULL OR btrim(house_number) <> ''),

    CONSTRAINT chk_new_buildings_latitude
        CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),

    CONSTRAINT chk_new_buildings_longitude
        CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),

    CONSTRAINT chk_new_buildings_banner_image_path_not_blank
        CHECK (banner_image_path IS NULL OR btrim(banner_image_path) <> ''),

    CONSTRAINT chk_new_buildings_panorama_image_path_not_blank
        CHECK (panorama_image_path IS NULL OR btrim(panorama_image_path) <> '')
);

CREATE INDEX inx_new_buildings_name ON new_buildings (name);
CREATE INDEX inx_new_buildings_sort_order ON new_buildings (sort_order);
CREATE INDEX inx_new_buildings_is_active ON new_buildings (is_active);
CREATE INDEX inx_new_buildings_city ON new_buildings (city);
CREATE INDEX inx_new_buildings_district ON new_buildings (district);
CREATE INDEX inx_new_buildings_created_at ON new_buildings (created_at);

CREATE TRIGGER trg_new_buildings_set_updated_at
    BEFORE UPDATE
    ON new_buildings
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();