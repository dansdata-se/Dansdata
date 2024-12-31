mod bands;
mod config;

pub use bands::*;
pub use config::*;
use secrecy::ExposeSecret;
use sqlx::postgres::{PgConnectOptions, PgPoolOptions};
use sqlx::PgPool;
use std::time::Duration;

pub async fn apply_database_migrations(config: &Config) -> Result<(), sqlx::Error> {
    let pool = PgPoolOptions::new()
        .acquire_timeout(Duration::from_secs(3))
        .connect_with(
            PgConnectOptions::new()
                .host(&config.host)
                .port(config.port)
                .database(&config.database)
                .username(&config.migration_user)
                .password(config.migration_password.expose_secret()),
        )
        .await?;

    sqlx::migrate!("./migrations").run(&pool).await?;

    Ok(())
}

pub async fn connect(config: &Config) -> Result<PgPool, sqlx::Error> {
    PgPoolOptions::new()
        .max_connections(20)
        .acquire_timeout(Duration::from_secs(3))
        .connect_with(
            PgConnectOptions::new()
                .host(&config.host)
                .port(config.port)
                .database(&config.database)
                .username(&config.user)
                .password(config.password.expose_secret()),
        )
        .await
}
