#!/usr/bin/env bash
set -euo pipefail

cd /docker-entrypoint-initdb.d/

# Import helpers from docker-entrypoint.sh
# (this file has a specific check to prevent
# it from executing when being sourced)
# https://github.com/docker-library/postgres/blob/master/17/bookworm/docker-entrypoint.sh
source /usr/local/bin/docker-entrypoint.sh

file_env 'DB_NAME'

docker_process_sql \
  --dbname="$DB_NAME" \
  -f /tmp/migrate_latest.sql
