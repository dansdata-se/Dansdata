use crate::graphql::{build_schema, DansdataSchema};
use crate::server::routes::MethodHandler;
use async_graphql::http::{Credentials, GraphiQLSource};
use async_graphql_axum::GraphQL;
use axum::http::Method;
use axum::response::Html;
use axum::routing::get;
use axum::Router;
use tower_http::cors::{Any, CorsLayer};
use tracing::instrument;

async fn get_handler() -> MethodHandler<Html<String>> {
    Ok(Html(
        GraphiQLSource::build()
            .endpoint("/graphql")
            .title("GraphiQL IDE | Dansdata")
            .header("Authorization", "Bearer abc123")
            .credentials(Credentials::Include)
            .finish(),
    ))
}

fn build_post_service() -> GraphQL<DansdataSchema> {
    let schema = build_schema()
        .limit_depth(5)
        .limit_complexity(50)
        .limit_recursive_depth(5)
        .limit_directives(50)
        .finish();
    GraphQL::new(schema)
}

#[instrument(level = "trace", skip_all)]
pub(crate) fn configure() -> Router {
    Router::new()
        .route("/", get(get_handler).post_service(build_post_service()))
        .layer(
            CorsLayer::new()
                .allow_origin(Any)
                .allow_methods([Method::GET, Method::POST]),
        )
}
