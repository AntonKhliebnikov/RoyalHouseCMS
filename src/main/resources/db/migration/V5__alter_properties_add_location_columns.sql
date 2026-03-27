ALTER TABLE properties
    ADD COLUMN city             VARCHAR(120),
    ADD COLUMN district         VARCHAR(120),
    ADD COLUMN street           VARCHAR(150),
    ADD COLUMN house_number     VARCHAR(30),
    ADD COLUMN building_section VARCHAR(50),
    ADD COLUMN unit_number      VARCHAR(30),
    ADD COLUMN latitude         NUMERIC(9, 6),
    ADD COLUMN longitude        NUMERIC(9, 6);

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_city_not_blank
        CHECK ( properties.city IS NULL OR btrim(properties.city) <> '');

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_district_not_blank
        CHECK ( properties.district IS NULL OR btrim(properties.district) <> '');

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_street_not_blank
        CHECK ( properties.street IS NULL OR btrim(properties.street) <> '');

ALTER TABLE properties
    ADD CONSTRAINT chk_house_number_not_blank
        CHECK ( properties.house_number IS NULL OR btrim(properties.house_number) <> '');

ALTER TABLE properties
    ADD CONSTRAINT chk_building_section_not_blank
        CHECK ( properties.building_section IS NULL OR btrim(properties.building_section) <> '');

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_unit_number_not_blank
        CHECK (unit_number IS NULL OR btrim(unit_number) <> '');

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_latitude
        CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90);

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_longitude
        CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180);

CREATE INDEX inx_properties_city ON properties (city);
CREATE INDEX inx_properties_district ON properties (district);
CREATE INDEX inx_properties_latitude_longitude ON properties (latitude, longitude);