CREATE TABLE IF NOT EXISTS translations.languages (
    code TEXT NOT NULL
        CONSTRAINT languages_pk PRIMARY KEY,
    name_id INTEGER NOT NULL
        CONSTRAINT languages_name_id_metadata_id_fk
            REFERENCES translations.metadatas
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

COMMENT ON TABLE translations.languages IS 'A list of all languages known to the system.';

COMMENT ON COLUMN translations.languages.code IS 'ISO 639-1 Alpha-2 language code';

COMMENT ON CONSTRAINT languages_name_id_metadata_id_fk ON translations.languages IS 'Reference to a translated string containing the name of this language.';
