use crate::domain::EventNode;
use async_trait::async_trait;
use uuid::Uuid;

pub trait BandNode: Sync + Send {
    fn id(&self) -> Uuid;
    fn name(&self) -> &str;
}

#[async_trait]
pub trait BandEdge: Sync + Send {
    async fn events(&self) -> Vec<Box<dyn EventNode>>;
}

#[async_trait]
pub trait BandRepository: Sync + Send {
    async fn by_id(&self, id: Uuid) -> Option<Box<dyn BandNode>>;
    async fn all_bands(&self) -> Vec<Box<dyn BandNode>>;
}
