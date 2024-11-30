CREATE OR REPLACE PROCEDURE translations_public.set_translation(
  translation_id uuid,
  language_code TEXT,
  value TEXT
)
  SECURITY DEFINER
  LANGUAGE sql AS
$$
INSERT INTO translations_private.values AS v
  (
    metadata_id,
    language_id,
    value
  )
SELECT
  m.id,
  l.id,
  CASE
    WHEN l.code = set_translation.language_code
      THEN set_translation.value
    ELSE t.value
  END
  FROM
    translations_private.metadatas m
      INNER JOIN translations_public.translations t
      ON m.external_id = t.id
      INNER JOIN translations_private.languages l
      ON l.code = t.language_code
  WHERE
    external_id = set_translation.translation_id
ON CONFLICT(metadata_id, language_id) DO UPDATE SET
  value = set_translation.value
  WHERE
    v.language_id = (
                    SELECT
                      id
                      FROM
                        translations_private.languages l
                      WHERE
                        l.code = set_translation.language_code
                    );

  -- Clean up any existing override translation.
UPDATE translations_private.metadatas
SET
  override_value = NULL
  WHERE
    external_id = translation_id;
$$;

COMMENT ON PROCEDURE translations_public.set_translation(translation_id UUID, language_code TEXT, value TEXT) IS 'Set the text to use as the translation for a single language.

If an override text exists for the translation, this text will be converted
into language-specific translations first.';

ALTER PROCEDURE translations_public.set_translation(translation_id UUID, language_code TEXT, "value" TEXT) OWNER TO translations_owner;

GRANT EXECUTE ON PROCEDURE translations_public.set_translation(translation_id UUID, language_code TEXT, "value" TEXT) TO edit_dance_info, moderate_dance_info;
