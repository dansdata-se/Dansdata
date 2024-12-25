use crate::graphql::band::BandQueries;
use async_graphql::{MergedObject, Object};

#[derive(MergedObject, Default)]
pub struct Query(BandQueries);

#[cfg(test)]
mod tests {
    use super::*;
    use crate::assert_gql;
    use crate::graphql::testing::FluidGqlAssertion;
    use async_graphql::{EmptyMutation, EmptySubscription, Schema};
    use serde_json::json;

    #[tokio::test]
    async fn hello_world() {
        assert_gql!(
            Schema::build(Query::default(), EmptyMutation, EmptySubscription).finish(),
            "{ helloWorld }"
        )
        .has_no_errors()
        .is_equal_to(json!({"helloWorld": "hello world"}));
    }
}
