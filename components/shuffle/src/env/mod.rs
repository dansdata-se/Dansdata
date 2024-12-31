use std::ffi::OsString;
use std::fmt::{Display, Formatter};
use std::{fmt, fs};

/// Fetches the environment variable `key` from the current process.
///
/// If the environment variable is not defined, it will instead be read from the file specified by
/// the `${key}_FILE` variable.
pub fn var(name: &str) -> Result<String, VarError> {
    match std::env::var(name) {
        Ok(value) => Ok(value),
        Err(std::env::VarError::NotPresent) => from_secret_file(name),
        Err(e) => Err(e.into()),
    }
}

fn from_secret_file(name: &str) -> Result<String, VarError> {
    let path = std::env::var(format!("{name}_FILE"))?;
    fs::read_to_string(path).map_err(|e| e.into())
}

#[derive(Debug)]
pub enum VarError {
    NotPresent,
    NotUnicode(OsString),
    IoError(std::io::Error),
}

impl From<std::env::VarError> for VarError {
    fn from(value: std::env::VarError) -> Self {
        match value {
            std::env::VarError::NotPresent => VarError::NotPresent,
            std::env::VarError::NotUnicode(s) => VarError::NotUnicode(s),
        }
    }
}

impl From<std::io::Error> for VarError {
    fn from(value: std::io::Error) -> Self {
        VarError::IoError(value)
    }
}

impl Display for VarError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        match self {
            VarError::NotPresent => write!(f, "environment variable not found"),
            VarError::NotUnicode(ref s) => {
                write!(f, "environment variable was not valid unicode: {:?}", s)
            }
            VarError::IoError(e) => write!(f, "variable could not be read from file: {e}"),
        }
    }
}

impl std::error::Error for VarError {}
