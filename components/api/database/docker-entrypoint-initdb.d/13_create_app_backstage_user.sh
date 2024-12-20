#!/usr/bin/env bash
set -euo pipefail

cd /docker-entrypoint-initdb.d/

# Import helpers from docker-entrypoint.sh
# (this file has a specific check to prevent
# it from executing when being sourced)
# https://github.com/docker-library/postgres/blob/master/17/bookworm/docker-entrypoint.sh
source /usr/local/bin/docker-entrypoint.sh

file_env 'DB_APP_BACKSTAGE_USER'
file_env 'DB_APP_BACKSTAGE_PASSWORD'

# Create authentication user for applications
#
# This user is used temporarily by the application that actually connects to
# our database (i.e. Postgraphile). It is expected that the connecting
# appliation will call `SET ROLE` after authentication to downgrade their
# access to the level actually granted to the user being served.
docker_process_sql \
  -v USERNAME="$DB_APP_BACKSTAGE_USER" \
  -v PASSWORD="$DB_APP_BACKSTAGE_PASSWORD" \
  -f templates/create_user.sql

# Must grant highest privilege that may be required by the application.
# The privileges can then be downgraded at runtime using `SET ROLE`.
docker_process_sql \
  -v USERNAME="$DB_APP_BACKSTAGE_USER" \
  <<< 'GRANT moderate_dance_info TO :"USERNAME"'
