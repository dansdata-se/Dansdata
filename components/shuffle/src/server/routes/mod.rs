mod graphql;

use axum::response::IntoResponse;
use axum::routing::get;
use axum::{Json, Router};
use serde_json::{json, Value};
use tracing::instrument;

#[allow(type_alias_bounds)]
pub type MethodHandler<T: IntoResponse> = axum::response::Result<T>;

async fn get_handler() -> MethodHandler<Json<Value>> {
    Ok(Json(json!({"message": "Hello, World!"})))
}

#[instrument(level = "trace", skip_all)]
pub(crate) fn configure() -> Router {
    Router::new()
        .route("/", get(get_handler))
        .nest("/graphql", graphql::configure())
}
