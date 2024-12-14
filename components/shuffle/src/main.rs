mod built_info;
mod logging;

use crate::logging::init_logging;
use axum::routing::get;
use axum::{Json, Router};
use serde_json::json;
use tower::ServiceBuilder;
use tower_http::trace::{DefaultMakeSpan, DefaultOnRequest, DefaultOnResponse, TraceLayer};
use tower_http::LatencyUnit;
use tracing::{info, Level};

#[tokio::main]
async fn main() {
    let _logging_guard = init_logging();

    info!(
        "Starting {} v{} ({}) built with {} at {} for {}",
        built_info::PKG_NAME,
        built_info::PKG_VERSION,
        built_info::GIT_COMMIT_HASH_SHORT.expect("Failed to get git commit hash"),
        built_info::RUSTC_VERSION,
        built::util::strptime(built_info::BUILT_TIME_UTC).to_rfc3339(),
        built_info::TARGET,
    );

    let app = Router::new()
        .route(
            "/",
            get(|| async { Json(json!({"message": "Hello, World!"})) }),
        )
        .layer(
            ServiceBuilder::new().layer(
                TraceLayer::new_for_http()
                    .make_span_with(DefaultMakeSpan::new().include_headers(true))
                    .on_request(DefaultOnRequest::new().level(Level::INFO))
                    .on_response(
                        DefaultOnResponse::new()
                            .level(Level::INFO)
                            .latency_unit(LatencyUnit::Micros),
                    ),
            ),
        );

    let listener = tokio::net::TcpListener::bind("127.0.0.1:8080")
        .await
        .unwrap();
    axum::serve(listener, app).await.unwrap();
}
