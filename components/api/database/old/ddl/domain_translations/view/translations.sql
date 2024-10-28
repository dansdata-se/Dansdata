CREATE OR REPLACE VIEW translations.translations AS
    SELECT
        m.id AS metadata,
        l.code AS code,
        COALESCE(m.override_value, v.value, '') AS value
        FROM
            metadatas m
                CROSS JOIN languages l
                LEFT JOIN values v
                    ON v.metadata_id = m.id AND v.language_code = l.code;

COMMENT ON VIEW translations.translations IS 'A view of all translated strings, with values for all languages.

Read operations should typically be performed on this view rather than the backing metadata/values tables.';
