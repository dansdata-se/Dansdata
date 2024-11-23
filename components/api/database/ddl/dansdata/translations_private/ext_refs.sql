CREATE TABLE IF NOT EXISTS translations_private.ext_refs (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT external_refs_pk
            PRIMARY KEY,
    uri TEXT NOT NULL
        UNIQUE
        CHECK (uri LIKE 'dansdata.entity://%')
);

COMMENT ON TABLE translations_private.ext_refs IS 'References to externally owned entities.';

COMMENT ON COLUMN translations_private.ext_refs.id IS 'Internal identifier for this reference to an external entity.';

COMMENT ON COLUMN translations_private.ext_refs.uri IS 'Globally unique URI reference for the external entity.

The expected protocol is `dansdata.entity://`';


ALTER TABLE translations_private.ext_refs
    OWNER TO translations_owner;
