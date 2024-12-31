mod built_info;
mod domain;
mod env;
mod graphql;
mod logging;
mod persistence;
mod server;

use crate::domain::BandRepository;
use crate::logging::{format_panic, init_logging};
use crate::persistence::{apply_database_migrations, BandSqlRepository};
use crate::server::Server;
use std::panic::catch_unwind;
use std::process::exit;
use tracing::{error, info, warn};

const LISTEN_ADDR_FALLBACK: &str = "127.0.0.1:8080";
const DATABASE_URL_FALLBACK: &str = "postgresql://dansdata:dansdata-pwd@db:5432/dansdata";

#[tokio::main]
async fn main() {
    let _logging_guard = match catch_unwind(|| init_logging()) {
        Ok(guard) => guard,
        Err(e) => {
            error!("Failed to configure logging: {}", format_panic(e));
            exit(1)
        }
    };

    info!(
        "Starting {} v{} ({}) built with {} at {} for {}",
        built_info::PKG_NAME,
        built_info::PKG_VERSION,
        built_info::GIT_COMMIT_HASH_SHORT
            .map(|x| x.to_string())
            .or_else(|| std::env::var("GIT_SHA").ok())
            .expect("Git commit hash should be available"),
        built_info::RUSTC_VERSION,
        built::util::strptime(built_info::BUILT_TIME_UTC).to_rfc3339(),
        built_info::TARGET,
    );

    let listen_addr = std::env::var("LISTEN_ADDR").unwrap_or_else(|_| {
        warn!("LISTEN_ADDR is not set. Falling back on {LISTEN_ADDR_FALLBACK}");
        LISTEN_ADDR_FALLBACK.to_string()
    });

    let persistence_config = match persistence::Config::from_env() {
        Ok(config) => config,
        Err(e) => {
            error!("Failed to load database config: {e}");
            exit(1)
        }
    };

    if let Err(e) = apply_database_migrations(&persistence_config).await {
        error!("Failed to apply database migrations: {e}");
        exit(1)
    }

    let pool = match persistence::connect(&persistence_config).await {
        Ok(pool) => pool,
        Err(e) => {
            error!("Failed to connect to database: {}", e);
            exit(1)
        }
    };

    let band_repository: Box<dyn BandRepository> =
        Box::new(BandSqlRepository::new(pool)) as Box<dyn BandRepository>;

    Server::new(band_repository).serve(&listen_addr).await;

    info!("Shutting down...");
}
