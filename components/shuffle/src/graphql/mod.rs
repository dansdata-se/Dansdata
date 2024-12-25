use crate::graphql::query::Query;
use async_graphql::{EmptyMutation, EmptySubscription, Schema, SchemaBuilder};

mod band;
mod query;
mod testing;

pub type DansdataSchema = Schema<Query, EmptyMutation, EmptySubscription>;
pub type DansdataSchemaBuilder = SchemaBuilder<Query, EmptyMutation, EmptySubscription>;

pub fn build_schema() -> DansdataSchemaBuilder {
    Schema::build(Query::default(), EmptyMutation, EmptySubscription)
}
