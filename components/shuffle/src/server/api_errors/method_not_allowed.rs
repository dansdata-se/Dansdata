use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use axum::Json;
use serde::{Deserialize, Serialize};

const CODE: &str = "METHOD_NOT_ALLOWED";
const MESSAGE: &str = "The request method is not allowed.";

#[derive(Debug, Serialize, Deserialize)]
pub struct MethodNotAllowedError {
    pub(crate) code: String,
    pub(crate) message: String,
}

#[allow(dead_code)]
impl MethodNotAllowedError {
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

impl IntoResponse for MethodNotAllowedError {
    fn into_response(self) -> Response {
        (StatusCode::METHOD_NOT_ALLOWED, Json(self)).into_response()
    }
}
