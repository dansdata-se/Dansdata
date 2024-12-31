FROM postgres:17-bookworm

ENV DEBIAN_FRONTEND=noninteractive

# Install
# * [Timescale](https://www.timescale.com/)
# * [PostGIS](https://postgis.net/)
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
RUN chmod 555 /docker-entrypoint-initdb.d/*.sh && chmod 555 /docker-entrypoint-initdb.d/templates

EXPOSE 5432
HEALTHCHECK --start-period=10s --start-interval=2s --interval=10s --timeout=5s --retries=3 \
  CMD pg_isready || exit 1
CMD ["postgres", "-c", "shared_preload_libraries=timescaledb", "-c", "log_statement=all", "-c", "log_destination=stderr"]
