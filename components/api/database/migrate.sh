#!/usr/bin/env bash
set -euxo

cat \
  ./ddl/dansdata/events_public/events_public.sql \
  ./ddl/dansdata/events_public/notify_entity_deleted.sql \
  ./ddl/dansdata/translations_private/translations_private.sql \
  ./ddl/dansdata/translations_private/ext_refs.sql \
  ./ddl/dansdata/translations_private/get_or_create_ext_ref.sql \
  ./ddl/dansdata/translations_private/metadatas.sql \
  ./ddl/dansdata/translations_private/languages.sql \
  ./ddl/dansdata/translations_private/values.sql \
  ./ddl/dansdata/translations_public/translations_public.sql \
  ./ddl/dansdata/translations_public/drop_ext_ref.sql \
  ./ddl/dansdata/translations_public/translations.sql \
  ./ddl/dansdata/translations_public/allocate_translation.sql \
  ./ddl/dansdata/translations_public/set_translation_override.sql \
  ./ddl/dansdata/translations_public/set_translation.sql \
  ./ddl/dansdata/translations_public/drop_translation.sql \
  ./seed.sql \
  > ./migrate_latest.sql

usql -f ./migrate_latest.sql pg://dansdata:dansdata-pwd@127.0.0.1:32768/dansdata
