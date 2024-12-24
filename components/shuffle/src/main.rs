mod built_info;
mod logging;
mod server;

use crate::logging::init_logging;
use crate::server::Server;
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

    Server::new().serve("127.0.0.1:8080").await;

    info!("Shutting down...");
}
