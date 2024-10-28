CREATE TABLE metadatas (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT metadatas_pk PRIMARY KEY,
    owner_id INTEGER NOT NULL
        CONSTRAINT metadatas_owner_id_external_refs_id_fk
            REFERENCES external_refs
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    override_value TEXT
);

COMMENT ON TABLE metadatas IS 'Contains metadata about a given translation.';

COMMENT ON COLUMN metadatas.owner_id IS 'Reference to the external owner of this translation.';

COMMENT ON COLUMN metadatas.override_value IS 'Translation to use fo all languages, or null to use a language-specific string.';

ALTER TABLE metadatas
    OWNER TO dansdata;

GRANT SELECT ON metadatas TO PUBLIC;

GRANT DELETE, INSERT, SELECT, UPDATE ON metadatas TO edit_dance_info;

GRANT DELETE, INSERT, SELECT, UPDATE ON metadatas TO moderate_dance_info;
