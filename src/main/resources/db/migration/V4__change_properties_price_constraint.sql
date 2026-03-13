ALTER TABLE properties
    DROP CONSTRAINT chk_properties_price;

ALTER TABLE properties
    ADD CONSTRAINT chk_properties_price
        CHECK (price > 0);