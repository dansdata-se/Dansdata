mod built_info;
mod domain;
mod graphql;
mod logging;
mod persistence;
mod server;

use crate::domain::BandRepository;
use crate::logging::init_logging;
use crate::persistence::BandSqlRepository;
use crate::server::Server;
use sqlx::PgPool;
use tracing::info;

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

    let pool = PgPool::connect("postgresql://postgres:mysecretpassword@localhost:5432/postgres")
        .await
        .expect("Failed to connect to database");
    Server::new(Box::new(BandSqlRepository::new(pool)) as Box<dyn BandRepository>)
        .serve("127.0.0.1:8080")
        .await;
    info!("Shutting down...");
}
