use crate::domain::{BandNode, BandRepository};
use crate::persistence::bands::entity::BandEntity;
use async_trait::async_trait;
use sqlx::PgPool;
use uuid::Uuid;

pub struct BandSqlRepository{
    pg: PgPool,
}

impl BandSqlRepository {
    pub fn new(pool: PgPool) -> Self {
        BandSqlRepository { pg: pool }
    }
}

#[async_trait]
impl BandRepository for BandSqlRepository {
    async fn by_id(&self, id: Uuid) -> Option<Box<dyn BandNode>> {
        sqlx::query_file_as!(BandEntity, "src/persistence/bands/select_by_id.sql", id)
            .fetch_one(&self.pg)
            .await
            .ok()
            .map(|entity| Box::new(entity) as Box<dyn BandNode>)
    }

    async fn all_bands(&self) -> Vec<Box<dyn BandNode>> {
        sqlx::query_file_as!(BandEntity, "src/persistence/bands/select_all.sql")
            .fetch_all(&self.pg)
            .await
            .map(|entities| {
                entities
                    .into_iter()
                    .map(|entity| Box::new(entity) as Box<dyn BandNode>)
                    .collect()
            })
            .unwrap_or_else(|_| Vec::new())
    }
}
