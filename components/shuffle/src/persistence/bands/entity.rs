use crate::domain::BandNode;
use uuid::Uuid;

#[derive(sqlx::FromRow)]
pub struct BandEntity {
    pub id: Uuid,
    pub name: String,
}

impl BandNode for BandEntity {
    fn id(&self) -> Uuid {
        self.id
    }

    fn name(&self) -> &str {
        self.name.as_str()
    }
}