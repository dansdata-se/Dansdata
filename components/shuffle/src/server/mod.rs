mod api_errors;
mod routes;

use crate::domain::BandRepository;
use crate::logging::format_panic;
use crate::server::api_errors::{MethodNotAllowedError, NotFoundError, UnknownInternalServerError};
use axum::http::{header, Response};
use axum::response::IntoResponse;
use axum::Router;
use std::any::Any;
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

pub struct Server {
    router: Router,
}

impl Server {
    pub fn new(band_repository: Box<dyn BandRepository>) -> Self {
        Self {
            router: Router::new()
                .nest("/", routes::configure(band_repository))
                .method_not_allowed_fallback(|| async { MethodNotAllowedError::new() })
                .fallback(|| async { NotFoundError::new() })
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
                        .layer(CatchPanicLayer::custom(
                            |err: Box<dyn Any + Send + 'static>| -> Response<_> {
                                error!("Service panicked: {}", format_panic(err));
                                UnknownInternalServerError::new().into_response()
                            },
                        ))
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
                ),
        }
    }

    pub async fn serve(&self, addr: &str) {
        let listener = match tokio::net::TcpListener::bind(addr).await {
            Ok(listener) => listener,
            Err(e) => {
                error!("Failed to bind to {}: {}", addr, e);
                return;
            }
        };

        info!("Listening on {}", listener.local_addr().unwrap());
        axum::serve(listener, self.router.clone())
            .with_graceful_shutdown(shutdown_signal())
            .await
            .unwrap();
    }
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
