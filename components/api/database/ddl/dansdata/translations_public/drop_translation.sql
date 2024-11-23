CREATE OR REPLACE PROCEDURE translations_public.drop_translation(translation_id uuid)
    SECURITY DEFINER
    LANGUAGE sql AS
$$
DELETE
    FROM
        translations_private.metadatas
    WHERE
        external_id = translation_id;
$$;

COMMENT ON PROCEDURE translations_public.drop_translation(translation_id uuid) IS 'Delete the given translation.';

ALTER PROCEDURE translations_public.drop_translation(translation_id uuid) OWNER TO translations_owner;
