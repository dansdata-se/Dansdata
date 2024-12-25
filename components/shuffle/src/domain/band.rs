use crate::domain::EventNode;
use uuid::Uuid;

pub trait BandNode {
    fn id(&self) -> Uuid;
    fn name(&self) -> &str;
}

pub trait BandEdges {
    fn events(&self) -> Vec<impl EventNode>;
}
