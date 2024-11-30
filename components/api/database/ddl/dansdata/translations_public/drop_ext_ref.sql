CREATE OR REPLACE PROCEDURE translations_public.drop_ext_ref(entity_uri TEXT)
  SECURITY DEFINER
  LANGUAGE sql AS
$$
DELETE
  FROM
    translations_private.ext_refs
  WHERE
    uri = entity_uri;
$$;

COMMENT ON PROCEDURE translations_public.drop_ext_ref(entity_uri TEXT) IS 'Delete any references to an external entity and release all associated resources.';

ALTER PROCEDURE translations_public.drop_ext_ref(entity_uri TEXT) OWNER TO translations_owner;
