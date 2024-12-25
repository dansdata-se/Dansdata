use crate::domain::EventNode;
use uuid::Uuid;

pub trait VenueNode {
    fn id(&self) -> Uuid;
    fn name(&self) -> &str;
    fn lat(&self) -> f64;
    fn lng(&self) -> f64;
    fn city(&self) -> Option<&str>;
    fn region(&self) -> Option<&str>;
}

pub trait VenueEdge {
    fn events(&self) -> Vec<impl EventNode>;
}
