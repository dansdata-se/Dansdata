use crate::domain::{BandNode, BandRepository};
use async_graphql::{Context, Object, ID};
use std::sync::Arc;

struct BandType(Arc<dyn BandNode>);

#[Object]
impl BandType {
    async fn id(&self) -> ID {
        self.0.id().into()
    }
    async fn name(&self) -> String {
        self.0.name().to_string()
    }
}

#[derive(Default)]
pub struct BandQueries;

#[Object]
impl BandQueries {
    async fn bands<'ctx>(&self, ctx: &Context<'ctx>) -> Vec<BandType> {
        ctx.data::<Arc<dyn BandRepository>>()
            .unwrap()
            .all_bands()
            .await
            .into_iter()
            .map(|band| BandType(band.into()))
            .collect()
    }
}
