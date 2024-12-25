use crate::domain::{BandNode, VenueNode};
use chrono::{NaiveDate, NaiveDateTime, NaiveTime, TimeZone};
use uuid::Uuid;

pub trait EventNode {
    fn id(&self) -> Uuid;
    fn start_date(&self) -> NaiveDate;
    fn start_time(&self) -> Option<NaiveTime>;
    fn end(&self) -> Option<NaiveDateTime>;
    fn timezone(&self) -> impl TimeZone;
}

pub trait EventEdge {
    fn bands(&self) -> Vec<impl BandNode>;
    fn venue(&self) -> impl VenueNode;
}
