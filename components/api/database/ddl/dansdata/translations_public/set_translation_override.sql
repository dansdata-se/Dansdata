CREATE OR REPLACE PROCEDURE translations_public.set_translation_override(
    translation_id uuid,
    value TEXT
)
    SECURITY DEFINER
    LANGUAGE sql AS
$$
UPDATE translations_private.metadatas
SET
    override_value = set_translation_override.value
    WHERE
        external_id = translation_id;


    -- Clean up any existing per-language definitions.
DELETE
    FROM
        translations_private.values
    WHERE
        metadata_id = (
                      SELECT
                          id
                          FROM
                              translations_private.metadatas m
                          WHERE
                              m.external_id = translation_id
                      );
$$;

COMMENT ON PROCEDURE translations_public.set_translation_override(translation_id UUID, value TEXT) IS 'Set the text to use as the translation for all languages.';

ALTER PROCEDURE translations_public.set_translation_override(translation_id UUID, "value" TEXT) OWNER TO translations_owner;

GRANT EXECUTE ON PROCEDURE translations_public.set_translation_override(translation_id UUID, "value" TEXT) TO edit_dance_info, moderate_dance_info;
