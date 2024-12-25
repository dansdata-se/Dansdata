CREATE TABLE dansdata.event_bands (
    event_id INTEGER NOT NULL
        CONSTRAINT event_bands_event_id_events_id_fk
            REFERENCES dansdata.events,
    band_id INTEGER NOT NULL
        CONSTRAINT event_bands_band_id_bands_id_fk
            REFERENCES dansdata.bands,
    CONSTRAINT event_bands_pk
        PRIMARY KEY (event_id, band_id)
);

COMMENT ON TABLE dansdata.event_bands IS 'Relates bands to the events they perform at.';

CREATE INDEX event_bands_event_id_idx ON dansdata.event_bands (event_id);

CREATE INDEX event_bands_band_id_idx ON dansdata.event_bands (band_id);
