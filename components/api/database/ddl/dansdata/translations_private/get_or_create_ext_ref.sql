CREATE OR REPLACE FUNCTION translations_private.get_or_create_ext_ref(entity_uri TEXT) RETURNS INTEGER
  LANGUAGE sql AS
$$
WITH
  allocate AS ( INSERT INTO translations_private.ext_refs (uri) VALUES
                                                                  (
                                                                    get_or_create_ext_ref.entity_uri
                                                                  ) ON CONFLICT DO NOTHING RETURNING id
              )
SELECT *
  FROM
    allocate
UNION
SELECT
  id
  FROM
    translations_private.ext_refs
  WHERE
    translations_private.ext_refs.uri = get_or_create_ext_ref.entity_uri;
$$;

COMMENT ON FUNCTION translations_private.get_or_create_ext_ref(entity_uri TEXT) IS 'Retrieves the internal id to use for the given external entity, generating one if such an id does not already exist.';

ALTER FUNCTION translations_private.get_or_create_ext_ref(entity_uri TEXT) OWNER TO translations_owner;
