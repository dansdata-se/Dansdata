CREATE OR REPLACE PROCEDURE events_public.notify_entity_deleted(entity_uri TEXT)
  LANGUAGE sql AS
$$
SELECT pg_notify('dansdata_events_v1'::TEXT, JSON_BUILD_OBJECT('type', 'delete', 'entityUri', entity_uri)::TEXT);
$$;

COMMENT ON PROCEDURE events_public.notify_entity_deleted(entity_uri TEXT) IS 'Emit a notification that the specified entity was deleted.';

GRANT EXECUTE ON PROCEDURE events_public.notify_entity_deleted TO event_emitter;
