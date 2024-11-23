CREATE OR REPLACE FUNCTION translations_public.allocate_translation(entity_uri TEXT) RETURNS uuid
    SECURITY DEFINER
    LANGUAGE sql AS
$$
INSERT INTO translations_private.metadatas(
    owner_id
)
SELECT
    translations_private.get_or_create_ext_ref(entity_uri)
    RETURNING external_id;
$$;

COMMENT ON FUNCTION translations_public.allocate_translation(entity_uri TEXT) IS 'Allocates a new translation id for use by the given owner.';

ALTER FUNCTION translations_public.allocate_translation(entity_uri TEXT) OWNER TO translations_owner;
