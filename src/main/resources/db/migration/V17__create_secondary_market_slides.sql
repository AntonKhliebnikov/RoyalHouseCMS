CREATE TABLE secondary_market_slides
(
    id          BIGSERIAL PRIMARY KEY,
    image_path  VARCHAR(500) NOT NULL,
    banner_text VARCHAR(255),
    link_url    VARCHAR(500),
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_secondary_market_slides_sort_order_non_negative
        CHECK (sort_order >= 0)
);


CREATE TRIGGER trg_secondary_market_slides_set_updated_at
    BEFORE UPDATE
    ON secondary_market_slides
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();