mod built_info;
mod logging;

use crate::logging::init_logging;
use axum::http::header;
use axum::routing::get;
use axum::{Json, Router};
use serde_json::json;
use std::time::Duration;
use tokio::signal;
use tower::ServiceBuilder;
use tower_http::catch_panic::CatchPanicLayer;
use tower_http::compression::CompressionLayer;
use tower_http::request_id::{MakeRequestUuid, PropagateRequestIdLayer, SetRequestIdLayer};
use tower_http::sensitive_headers::SetSensitiveRequestHeadersLayer;
use tower_http::timeout::TimeoutLayer;
use tower_http::trace::{DefaultMakeSpan, DefaultOnRequest, DefaultOnResponse, TraceLayer};
use tower_http::LatencyUnit;
use tracing::{error, info, Level};

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
            ServiceBuilder::new()
                .layer(SetSensitiveRequestHeadersLayer::new(vec![
                    header::AUTHORIZATION,
                    header::COOKIE,
                ]))
                .layer(SetRequestIdLayer::x_request_id(MakeRequestUuid::default()))
                .layer(PropagateRequestIdLayer::x_request_id())
                .layer(CompressionLayer::new())
                .layer(TimeoutLayer::new(Duration::from_secs(10)))
                .layer(CatchPanicLayer::new())
                .layer(
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

    let bind_to = "127.0.0.1:8080";
    let listener = match tokio::net::TcpListener::bind(bind_to).await {
        Ok(listener) => listener,
        Err(e) => {
            error!("Failed to bind to {}: {}", bind_to, e);
            return;
        }
    };

    info!("Listening on {}", listener.local_addr().unwrap());
    axum::serve(listener, app)
        .with_graceful_shutdown(shutdown_signal())
        .await
        .unwrap();
}

async fn shutdown_signal() {
    let ctrl_c = async {
        signal::ctrl_c()
            .await
            .expect("Failed to install Ctrl+C handler");
    };

    #[cfg(unix)]
    let terminate = async {
        signal::unix::signal(signal::unix::SignalKind::terminate())
            .expect("Failed to install signal handler")
            .recv()
            .await;
    };

    #[cfg(not(unix))]
    let terminate = std::future::pending::<()>();

    tokio::select! {
        _ = ctrl_c => {},
        _ = terminate => {},
    }

    info!("Shutdown signal received");
}
