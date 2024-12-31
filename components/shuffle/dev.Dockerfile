FROM rust:1.83-bookworm
WORKDIR /app

EXPOSE 8080
HEALTHCHECK --start-period=10s --start-interval=2s --interval=10s --timeout=5s --retries=3 \
  CMD curl --output /dev/null --silent --head --fail http://127.0.0.1:8080
