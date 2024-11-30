CREATE TABLE IF NOT EXISTS translations_private.metadatas (
  id INTEGER GENERATED ALWAYS AS IDENTITY
    CONSTRAINT metadatas_pk
      PRIMARY KEY,
  external_id uuid DEFAULT gen_random_uuid() NOT NULL
    CONSTRAINT metadatas_ext_id_uniq
      UNIQUE,
  owner_id INTEGER NOT NULL
    CONSTRAINT metadatas_owner_id_external_refs_id_fk
      REFERENCES translations_private.ext_refs
      ON UPDATE CASCADE
      ON DELETE CASCADE,
  override_value TEXT
);

COMMENT ON TABLE translations_private.metadatas IS 'Contains metadata about a given translation.';

COMMENT ON COLUMN translations_private.metadatas.external_id IS 'Id that can be used by external entities to refer to this translation.';

COMMENT ON COLUMN translations_private.metadatas.owner_id IS 'Reference to the external owner of this translation.';

COMMENT ON COLUMN translations_private.metadatas.override_value IS 'Translation to use fo all languages, or null to use a language-specific string.';

ALTER TABLE translations_private.metadatas
  OWNER TO translations_owner;

CREATE OR REPLACE FUNCTION translations_private.metadatas_trigger_notify_translation_was_deleted() RETURNS TRIGGER
  LANGUAGE plpgsql AS
$$
BEGIN
  CALL events_public.notify_entity_deleted(('dansdata.entity://dansdata.se/v1/translations/translation?id='
    || old.external_id)::TEXT);
  RETURN NULL;
END;
$$;

CREATE OR REPLACE TRIGGER metadatas_trigger_notify_translation_was_deleted
  AFTER DELETE
  ON translations_private.metadatas
  FOR EACH ROW
EXECUTE PROCEDURE translations_private.metadatas_trigger_notify_translation_was_deleted();
