FROM lukemathwalker/cargo-chef:0.1.68-rust-1.83-slim-bookworm AS chef
WORKDIR /app

FROM chef AS planner
COPY . .
RUN cargo chef prepare --recipe-path recipe.json

FROM chef AS builder
COPY --from=planner /app/recipe.json .
RUN cargo chef cook --release --recipe-path recipe.json
COPY . .
RUN cargo build --release --target x86_64-unknown-linux-gnu --bin shuffle

FROM debian:bookworm-slim AS runtime
WORKDIR /app
COPY --from=builder /app/target/x86_64-unknown-linux-gnu/release/shuffle /usr/local/bin/

RUN set -eux; \
  groupadd -r dansdata; \
  useradd -r -g dansdata dansdata
USER dansdata

EXPOSE 8080
HEALTHCHECK --start-period=10s --start-interval=2s --interval=10s --timeout=5s --retries=3 \
  CMD curl --output /dev/null --silent --head --fail http://shuffle-api:8080
ENTRYPOINT ["/usr/local/bin/shuffle"]
