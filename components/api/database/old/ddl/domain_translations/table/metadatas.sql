CREATE TABLE IF NOT EXISTS translations.metadatas (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT metadata_pk PRIMARY KEY,
    owner_id INTEGER NOT NULL
        CONSTRAINT metadatas_owner_id_external_refs_id_fk
            REFERENCES translations.ext_refs
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    override_value TEXT
);

COMMENT ON TABLE translations.metadatas IS 'Contains metadata about a given translation.';

COMMENT ON COLUMN translations.metadatas.owner_id IS 'Reference to the external owner of this translation.';

COMMENT ON COLUMN translations.metadatas.override_value IS 'Translation to use for all languages, or null to use a language-specific string.';
