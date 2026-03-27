CREATE TABLE new_building_about_slides
(
    id              BIGSERIAL PRIMARY KEY,
    new_building_id BIGINT       NOT NULL,
    slide_number    SMALLINT     NOT NULL,
    image_path      VARCHAR(500) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT fk_new_building_about_slides_new_building
        FOREIGN KEY (new_building_id)
            REFERENCES new_buildings (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_new_building_about_slides_slide_number
        CHECK (slide_number BETWEEN 1 AND 3),

    CONSTRAINT chk_new_building_about_slides_image_path_not_blank
        CHECK (btrim(image_path) <> ''),

    CONSTRAINT uk_new_building_about_slides_building_slide
        UNIQUE (new_building_id, slide_number)
);

CREATE TRIGGER trg_new_building_about_slides_set_updated_at
    BEFORE UPDATE
    ON new_building_about_slides
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();