FROM debian:bookworm-slim as build-migration
WORKDIR /tmp/migrations

COPY ./ddl /tmp/migrations/ddl
COPY ./seed.sql /tmp/migrations/
COPY migrate.sh /tmp/migrations/

RUN ./migrate.sh

FROM postgres:17-bookworm

ENV DEBIAN_FRONTEND=noninteractive
ENV DBMS_OWNER_USER="postgres"
ENV DBMS_OWNER_PASSWORD="postgres-pwd"
ENV DB_OWNER_USER="dansdata"
ENV DB_OWNER_PASSWORD="dansdata-pwd"
ENV DB_APP_BACKSTAGE_USER="backstage"
ENV DB_APP_BACKSTAGE_PASSWORD="backstage-pwd"
ENV DB_NAME="dansdata"

# Postgres variables
ENV POSTGRES_USER="$DBMS_OWNER_USER"
ENV POSTGRES_PASSWORD="$DBMS_OWNER_PASSWORD"
ENV POSTGRES_DB="postgres"

RUN set -eux \
  && apt-get update \
  && apt-get install -y --no-install-recommends \
    ca-certificates \
    wget \
    lsb-release \
  && echo "deb https://packagecloud.io/timescale/timescaledb/debian $(lsb_release -c -s) main" | tee /etc/apt/sources.list.d/timescaledb.list \
  && wget -qO - https://packagecloud.io/timescale/timescaledb/gpgkey | gpg --dearmor -o /etc/apt/trusted.gpg.d/timescaledb.gpg \
  && apt-get update \
  && apt-get install -y --no-install-recommends \
    postgresql-17-postgis-3 \
    timescaledb-2-postgresql-17 \
  && rm -rf /var/lib/apt/lists/*

COPY --chmod=444 ./docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
COPY --chmod=444 --from=build-migration /tmp/migrations/migrate_latest.sql /tmp/migrate_latest.sql
RUN chmod 555 /docker-entrypoint-initdb.d/*.sh && chmod 555 /docker-entrypoint-initdb.d/templates

EXPOSE 5432
CMD ["postgres", "-c", "shared_preload_libraries=timescaledb", "-c", "log_statement=all", "-c", "log_destination=stderr"]
