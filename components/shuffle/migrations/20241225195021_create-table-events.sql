CREATE TABLE dansdata.events (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT events_pk
            PRIMARY KEY,
    key uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT events_key_uniq
            UNIQUE,
    venue_id INTEGER
        CONSTRAINT events_venue_id_venues_id_fk
            REFERENCES dansdata.venues,
    start timestamptz NOT NULL,
    "end" timestamptz,
    has_start_time BOOLEAN NOT NULL,
    created_at timestamptz DEFAULT timezone('utc', NOW()) NOT NULL
);

COMMENT ON COLUMN dansdata.events.id IS 'Used for database-internal relations.';

COMMENT ON COLUMN dansdata.events.key IS 'Uniquely identifies this entity to users outside this database.';

COMMENT ON COLUMN dansdata.events.start IS 'The date and time at which this event starts.';

COMMENT ON COLUMN dansdata.events.has_start_time IS 'Whether the time value in the `start` column is usable or not. Note that the start time should be set to 00:00 _in the event''s local timezone_ if this value is false.';

CREATE INDEX events_venue_id_idx ON dansdata.events (venue_id);

CREATE INDEX events_start_idx ON dansdata.events (start);

CREATE INDEX events_end_idx ON dansdata.events ("end");
