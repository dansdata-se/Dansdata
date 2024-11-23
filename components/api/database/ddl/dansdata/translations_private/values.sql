CREATE TABLE IF NOT EXISTS translations_private.values (
    metadata_id INTEGER NOT NULL
        CONSTRAINT values_metadata_id_metadatas_id_fk
            REFERENCES translations_private.metadatas
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    language_id INTEGER NOT NULL
        CONSTRAINT values_language_id_languages_id_fk
            REFERENCES translations_private.languages
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    value TEXT NOT NULL,
    CONSTRAINT values_pk
        PRIMARY KEY (language_id, metadata_id)
);

COMMENT ON TABLE translations_private.values IS 'Contains the translated texts for translations_private.';

COMMENT ON COLUMN translations_private.values.language_id IS 'Reference to the language this text is written in.';

COMMENT ON COLUMN translations_private.values.value IS 'The translated text.';

ALTER TABLE translations_private.values
    OWNER TO translations_owner;
