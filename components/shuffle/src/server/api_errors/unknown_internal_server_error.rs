use axum::http::StatusCode;
use axum::response::{IntoResponse, Response};
use axum::Json;
use serde::{Deserialize, Serialize};

const CODE: &str = "INTERNAL_SERVER_ERROR";
const MESSAGE: &str = "An internal error occurred. Please try again later.";

#[derive(Debug, Serialize, Deserialize)]
pub struct UnknownInternalServerError {
    pub(crate) code: String,
    pub(crate) message: String,
}

#[allow(dead_code)]
impl UnknownInternalServerError {
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

impl IntoResponse for UnknownInternalServerError {
    fn into_response(self) -> Response {
        (StatusCode::INTERNAL_SERVER_ERROR, Json(self)).into_response()
    }
}
