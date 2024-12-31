use crate::env::VarError;
use secrecy::SecretString;
use std::fmt::{Display, Formatter};
use std::str::FromStr;

#[derive(Debug)]
pub struct Config {
    pub host: String,
    pub port: u16,
    pub database: String,
    pub user: String,
    pub password: SecretString,
    pub migration_user: String,
    pub migration_password: SecretString,
}

impl Config {
    pub fn from_env() -> Result<Self, ConfigError> {
        Ok(Self {
            host: envvar("DB_HOST")?,
            port: u16::from_str(envvar("DB_PORT").unwrap_or("5432".to_string()).as_str()).map_err(
                |e| ConfigError::Invalid {
                    key: "DB_PORT".to_string(),
                    message: format!("{e}"),
                },
            )?,
            database: envvar("DB_NAME")?,
            user: envvar("DB_APP_SHUFFLE_USER")?,
            password: envvar("DB_APP_SHUFFLE_PASSWORD")?.into(),
            migration_user: envvar("DB_MIGRATION_USER")?,
            migration_password: envvar("DB_MIGRATION_PASSWORD")?.into(),
        })
    }
}

fn envvar(key: &str) -> Result<String, ConfigError> {
    crate::env::var(key).map_err(|e| ConfigError::FailedToRead {
        key: key.to_string(),
        cause: e,
    })
}

#[derive(Debug)]
pub enum ConfigError {
    FailedToRead { key: String, cause: VarError },
    Invalid { key: String, message: String },
}

impl Display for ConfigError {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            ConfigError::FailedToRead { key, cause } => {
                write!(f, "failed to read variable '{key}': {cause}")
            }
            ConfigError::Invalid { key, message } => {
                write!(f, "invalid variable '{key}': {message}")
            }
        }
    }
}

impl std::error::Error for ConfigError {}
