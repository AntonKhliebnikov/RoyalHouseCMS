CREATE TABLE new_building_specification_blocks
(
    id              BIGSERIAL PRIMARY KEY,
    new_building_id BIGINT NOT NULL,
    sort_order      INTEGER NOT NULL,
    content         TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_new_building_specification_blocks_new_building
        FOREIGN KEY (new_building_id)
            REFERENCES new_buildings (id)
            ON DELETE CASCADE,

    CONSTRAINT chk_new_building_specification_blocks_sort_order
        CHECK (sort_order > 0),

    CONSTRAINT uk_new_building_specification_blocks_building_sort
        UNIQUE (new_building_id, sort_order)
);

CREATE TRIGGER trg_new_building_specification_blocks_set_updated_at
    BEFORE UPDATE
    ON new_building_specification_blocks
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();