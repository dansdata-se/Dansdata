CREATE OR REPLACE VIEW translations_public.translations(id, language_code, value) AS
    SELECT
        m.external_id AS id,
        l.code AS language_code,
        COALESCE(m.override_value, v.value, ''::TEXT) AS value
        FROM
            translations_private.metadatas m
                CROSS JOIN translations_private.languages l
                LEFT JOIN translations_private.values v
                    ON v.metadata_id = m.id AND v.language_id = l.id;

COMMENT ON VIEW translations_public.translations IS 'A view of all translated strings, with values for all languages.';

ALTER TABLE translations_public.translations
    OWNER TO translations_owner;

GRANT SELECT ON translations_public.translations TO PUBLIC;
