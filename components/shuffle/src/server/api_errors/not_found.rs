use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use axum::Json;
use serde::{Deserialize, Serialize};

const CODE: &str = "NOT_FOUND";
const MESSAGE: &str = "The requested resource could not be found.";

#[derive(Debug, Serialize, Deserialize)]
pub struct NotFoundError {
    pub(crate) code: String,
    pub(crate) message: String,
}

#[allow(dead_code)]
impl NotFoundError {
    pub fn new() -> Self {
        Self {
            code: CODE.to_string(),
            message: MESSAGE.to_string(),
        }
    }

    pub fn with_message(message: &str) -> Self {
        Self {
            code: CODE.to_string(),
            message: message.to_string(),
        }
    }
}

impl IntoResponse for NotFoundError {
    fn into_response(self) -> Response {
        (StatusCode::NOT_FOUND, Json(self)).into_response()
    }
}
