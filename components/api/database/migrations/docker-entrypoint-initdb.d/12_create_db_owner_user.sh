#!/usr/bin/env bash
set -euo pipefail

cd /docker-entrypoint-initdb.d/

# Import helpers from docker-entrypoint.sh
# (this file has a specific check to prevent
# it from executing when being sourced)
# https://github.com/docker-library/postgres/blob/master/17/bookworm/docker-entrypoint.sh
source /usr/local/bin/docker-entrypoint.sh

file_env 'DB_OWNER_USER'
file_env 'DB_OWNER_PASSWORD'

# Create database owner.
#
# This user is used to apply migrations and can be considered a local superuser
# for the application database.
docker_process_sql \
  -v USERNAME="$DB_OWNER_USER" \
  -v PASSWORD="$DB_OWNER_PASSWORD" \
  -f templates/create_user.sql


# Must grant highest privilege that may be required by the application.
# The privileges can then be downgraded at runtime using `SET ROLE`.
docker_process_sql \
  -v USERNAME="$DB_OWNER_USER" \
  <<< 'GRANT translations_owner TO :"USERNAME"';
