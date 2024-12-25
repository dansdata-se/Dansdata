#![cfg(test)]

use assert_json_diff::assert_json_eq;
use assertor::{assert_that, VecAssertion};
use async_graphql::Response;
use serde_json::Value;

pub struct FluidGqlAssertion {
    response: Response,
}

impl FluidGqlAssertion {
    pub fn new(response: Response) -> Self {
        Self { response }
    }

    pub fn has_no_errors(&self) -> &Self {
        assert_that!(self.response.errors).is_empty();
        self
    }

    pub fn is_equal_to(&self, json: Value) -> &Self {
        let json_body = self.response.data.clone().into_json();
        assert!(
            json_body.is_ok(),
            "Response is not valid json: {}",
            json_body.unwrap_err()
        );
        assert_json_eq!(json_body.unwrap(), json);
        self
    }
}

#[macro_export]
macro_rules! assert_gql {
    ($schema:expr, $query:literal) => {
        FluidGqlAssertion::new($schema.execute($query).await)
    };
}
