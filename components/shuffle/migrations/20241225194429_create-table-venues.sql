CREATE TABLE dansdata.venues (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT venues_pk
            PRIMARY KEY,
    key uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT venues_key_uniq
            UNIQUE,
--     coords geography NOT NULL,
    city TEXT,
    region TEXT,
    created_at timestamptz DEFAULT timezone('utc', NOW()) NOT NULL
);

COMMENT ON COLUMN dansdata.venues.id IS 'Used for database-internal relations.';

COMMENT ON COLUMN dansdata.venues.key IS 'Uniquely identifies this entity to users outside this database.';

COMMENT ON COLUMN dansdata.venues.city IS '(Closest) city where this venue is located. E.g. "Ljungsbro" or "Stockholm"';

COMMENT ON COLUMN dansdata.venues.region IS 'General region where this venue is located, e.g. "Östergötland" or "Skåne"';

CREATE INDEX venues_city_idx ON dansdata.venues (city);

-- CREATE INDEX venues_coords_geo_idx ON dansdata.venues USING gist (coords);

CREATE INDEX venues_region_idx ON dansdata.venues (region);
