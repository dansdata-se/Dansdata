CREATE TABLE IF NOT EXISTS translations.external_refs (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT external_refs_pk PRIMARY KEY,
    external_id TEXT NOT NULL,
    external_type TEXT NOT NULL,
    CONSTRAINT external_refs_external_id_external_type_uniq UNIQUE (external_id, external_type)
);

COMMENT ON TABLE translations.ext_refs IS 'References to externally owned objects.';

COMMENT ON COLUMN translations.ext_refs.id IS 'Internal identifier for this reference to an external object.';

COMMENT ON COLUMN translations.ext_refs.external_id IS 'Externally owned identifier. Must be unique for a given (external_id, external_type) pair.';

COMMENT ON COLUMN translations.ext_refs.external_type IS 'Type identifier of the external object.';
