CREATE TABLE new_building_infographics
(
    id              BIGSERIAL PRIMARY KEY,
    new_building_id BIGINT NOT NULL,
    section         VARCHAR(30) NOT NULL,
    sort_order      INTEGER NOT NULL,
    image_path      VARCHAR(500) NOT NULL,
    description     VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_new_building_infographics_new_building
        FOREIGN KEY (new_building_id)
            REFERENCES new_buildings (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_new_building_infographics_section
        CHECK (section IN ('BASIC', 'INFRASTRUCTURE', 'APARTMENTS')),

    CONSTRAINT chk_new_building_infographics_sort_order
        CHECK (sort_order > 0),

    CONSTRAINT chk_new_building_infographics_image_path_not_blank
        CHECK (btrim(image_path) <> ''),

    CONSTRAINT chk_new_building_infographics_description_not_blank
        CHECK (description IS NULL OR btrim(description) <> ''),

    CONSTRAINT uk_new_building_infographics_building_section_sort
        UNIQUE (new_building_id, section, sort_order)
);

CREATE TRIGGER trg_new_building_infographics_set_updated_at
    BEFORE UPDATE
    ON new_building_infographics
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();