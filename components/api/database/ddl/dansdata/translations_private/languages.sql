CREATE TABLE IF NOT EXISTS translations_private.languages (
  id INTEGER GENERATED ALWAYS AS IDENTITY
    CONSTRAINT languages_pk
      PRIMARY KEY,
  code TEXT NOT NULL
    CONSTRAINT languages_code_uniq
      UNIQUE,
  name_id INTEGER NOT NULL
    CONSTRAINT languages_name_id_metadatas_id_fk
      REFERENCES translations_private.metadatas
      ON UPDATE CASCADE
      ON DELETE CASCADE
);

COMMENT ON TABLE translations_private.languages IS 'A list of all languages known to the system.';

COMMENT ON COLUMN translations_private.languages.code IS 'ISO 639-1 Alpha-2 language code.';

COMMENT ON COLUMN translations_private.languages.name_id IS 'Reference to a translated string containing the name of this language.';

ALTER TABLE translations_private.languages
  OWNER TO translations_owner;
