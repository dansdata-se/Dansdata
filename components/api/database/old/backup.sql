CREATE SCHEMA domain_events;

CREATE SCHEMA translations;

COMMENT ON SCHEMA translations IS 'Domain layer for string translations_private';

CREATE TYPE translations.EXT_REF_TYPE AS ENUM ('language');

CREATE TABLE translations.external_refs (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT external_refs_pk PRIMARY KEY,
    external_id TEXT NOT NULL,
    external_type translations.EXT_REF_TYPE NOT NULL,
    CONSTRAINT external_refs_external_id_external_type_uniq UNIQUE (external_id, external_type)
);

COMMENT ON TABLE translations.ext_refs IS 'References to externally owned objects.';

COMMENT ON COLUMN translations.ext_refs.id IS 'Internal identifier for this reference to an external object.';

COMMENT ON COLUMN translations.ext_refs.external_id IS 'Externally owned identifier. Must be unique for a given (external_id, external_type) pair.';

COMMENT ON COLUMN translations.ext_refs.external_type IS 'Type identifier of the external object.';

GRANT SELECT ON translations.ext_refs TO PUBLIC;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.ext_refs TO edit_dance_info;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.ext_refs TO moderate_dance_info;

CREATE TABLE translations.metadatas (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT metadatas_pk PRIMARY KEY,
    owner_id INTEGER NOT NULL
        CONSTRAINT metadatas_owner_id_external_refs_id_fk
            REFERENCES translations.ext_refs
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    override_value TEXT
);

COMMENT ON TABLE translations.metadatas IS 'Contains metadata about a given translation.';

COMMENT ON COLUMN translations.metadatas.owner_id IS 'Reference to the external owner of this translation.';

COMMENT ON COLUMN translations.metadatas.override_value IS 'Translation to use fo all languages, or null to use a language-specific string.';

CREATE TABLE translations.languages (
    id INTEGER GENERATED ALWAYS AS IDENTITY,
    code TEXT NOT NULL
        CONSTRAINT languages_pk UNIQUE,
    name_id INTEGER NOT NULL
        CONSTRAINT languages_name_id_metadatas_id_fk
            REFERENCES translations.metadatas
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

COMMENT ON TABLE translations.languages IS 'A list of all languages known to the system.';

COMMENT ON COLUMN translations.languages.code IS 'ISO 639-1 Alpha-2 language code.';

COMMENT ON COLUMN translations.languages.name_id IS 'Reference to a translated string containing the name of this language.';

GRANT SELECT ON translations.languages TO PUBLIC;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.languages TO moderate_dance_info;

GRANT SELECT ON translations.metadatas TO PUBLIC;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.metadatas TO edit_dance_info;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.metadatas TO moderate_dance_info;

CREATE TABLE translations.values (
    metadata_id INTEGER NOT NULL,
    language_id INTEGER NOT NULL,
    value TEXT NOT NULL,
    CONSTRAINT values_pk PRIMARY KEY (language_id, metadata_id)
);

COMMENT ON TABLE translations.values IS 'Contains the translated texts for translations_private.';

COMMENT ON COLUMN translations.values.language_id IS 'Reference to the language this text is written in.';

COMMENT ON COLUMN translations.values.value IS 'The translated text.';

GRANT SELECT ON translations.values TO PUBLIC;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.values TO edit_dance_info;

GRANT DELETE, INSERT, SELECT, UPDATE ON translations.values TO moderate_dance_info;

CREATE VIEW translations.translations(metadata, code, value) AS
    SELECT
        m.id AS metadata,
        l.code,
        COALESCE(m.override_value, v.value, ''::TEXT) AS value
        FROM
            translations.metadatas m
                CROSS JOIN translations.languages l
                LEFT JOIN translations.values v
                    ON v.metadata_id = m.id AND v.language_id = l.id;

COMMENT ON VIEW translations.translations IS 'A view of all translated strings, with values for all languages.

Read operations should typically be performed on this view rather than the backing metadata/values tables.';

GRANT SELECT ON translations.translations TO PUBLIC;
