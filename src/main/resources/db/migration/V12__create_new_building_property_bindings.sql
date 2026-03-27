CREATE TABLE new_building_property_bindings
(
    id              BIGSERIAL PRIMARY KEY,
    new_building_id BIGINT NOT NULL,
    property_id     BIGINT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_new_building_property_bindings_new_building
        FOREIGN KEY (new_building_id)
            REFERENCES new_buildings (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_new_building_property_bindings_property
        FOREIGN KEY (property_id)
            REFERENCES properties (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_new_building_property_bindings_property
        UNIQUE (property_id),

    CONSTRAINT uk_new_building_property_bindings_building_property
        UNIQUE (new_building_id, property_id)
);

CREATE INDEX inx_new_building_property_bindings_new_building_id
    ON new_building_property_bindings (new_building_id);

CREATE INDEX inx_new_building_property_bindings_created_at
    ON new_building_property_bindings (created_at);