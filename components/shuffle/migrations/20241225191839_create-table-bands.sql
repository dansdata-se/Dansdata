CREATE TABLE dansdata.bands (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT bands_pk
            PRIMARY KEY,
    key uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT bands_key_uniq
            UNIQUE,
    name TEXT NOT NULL,
    created_at timestamptz DEFAULT timezone('utc', NOW()) NOT NULL
);

COMMENT ON COLUMN dansdata.bands.id IS 'Used for database-internal relations.';

COMMENT ON COLUMN dansdata.bands.key IS 'Uniquely identifies this entity to users outside this database.';
