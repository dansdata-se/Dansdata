CREATE TABLE IF NOT EXISTS translations.values (
    metadata_id INT NOT NULL,
    language_code TEXT NOT NULL
        CONSTRAINT values_language_languages_code_fk
            REFERENCES translations.languages
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    value TEXT NOT NULL,
    CONSTRAINT values_pk PRIMARY KEY (metadata_id, language_code),
    CONSTRAINT values_metadata_metadatas_id_fk FOREIGN KEY (metadata_id)
        REFERENCES translations.metadatas
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

COMMENT ON TABLE translations.values IS 'Contains the translated texts for translations_private.';
