use crate::built_info;
use opentelemetry::trace::TracerProvider as _;
use opentelemetry::{global, KeyValue};
use opentelemetry_sdk::trace::TracerProvider;
use opentelemetry_sdk::Resource;
use opentelemetry_sdk::{
    metrics::{MeterProviderBuilder, PeriodicReader, SdkMeterProvider},
    runtime::Tokio,
};
use opentelemetry_semantic_conventions::{attribute, SCHEMA_URL};
use std::any::Any;
use tracing::Level;
use tracing_opentelemetry::{MetricsLayer, OpenTelemetryLayer};
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::layer::SubscriberExt;
use tracing_subscriber::util::SubscriberInitExt;

pub struct LoggingGuard {
    tracer_provider: TracerProvider,
    meter_provider: SdkMeterProvider,
}

impl Drop for LoggingGuard {
    fn drop(&mut self) {
        if let Err(err) = self.tracer_provider.shutdown() {
            eprintln!("{err:?}");
        }
        if let Err(err) = self.meter_provider.shutdown() {
            eprintln!("{err:?}");
        }
    }
}

pub fn format_panic(err: Box<dyn Any + Send>) -> String {
    if let Some(s) = err.downcast_ref::<String>() {
        s.to_owned()
    } else if let Some(s) = err.downcast_ref::<&str>() {
        s.to_string()
    } else {
        "Panic info could not be obtained.".to_string()
    }
}

pub fn init_logging() -> LoggingGuard {
    let tracer_provider = init_tracer_provider();
    let meter_provider = init_meter_provider();

    tracing_subscriber::registry()
        .with(tracing_subscriber::filter::LevelFilter::from_level(
            Level::DEBUG,
        ))
        .with(tracing_subscriber::fmt::layer().with_span_events(FmtSpan::FULL))
        .with(MetricsLayer::new(meter_provider.clone()))
        .with(OpenTelemetryLayer::new(
            tracer_provider.tracer(built_info::PKG_NAME),
        ))
        .init();

    LoggingGuard {
        tracer_provider,
        meter_provider,
    }
}

fn init_tracer_provider() -> TracerProvider {
    let exporter = opentelemetry_otlp::SpanExporter::builder()
        .with_tonic()
        .build()
        .unwrap();

    TracerProvider::builder()
        .with_batch_exporter(exporter, Tokio)
        .build()
}

fn init_meter_provider() -> SdkMeterProvider {
    let exporter = opentelemetry_otlp::MetricExporter::builder()
        .with_tonic()
        .with_temporality(opentelemetry_sdk::metrics::Temporality::default())
        .build()
        .unwrap();

    let reader = PeriodicReader::builder(exporter, Tokio)
        .with_interval(std::time::Duration::from_secs(30))
        .build();

    // For local development debugging
    let stdout_reader =
        PeriodicReader::builder(opentelemetry_stdout::MetricExporter::default(), Tokio).build();

    let meter_provider = MeterProviderBuilder::default()
        .with_resource(resource())
        .with_reader(reader)
        .with_reader(stdout_reader)
        .build();
    global::set_meter_provider(meter_provider.clone());

    meter_provider
}

fn resource() -> Resource {
    Resource::from_schema_url(
        [
            KeyValue::new(attribute::SERVICE_NAME, built_info::PKG_NAME),
            KeyValue::new(
                attribute::SERVICE_VERSION,
                format!(
                    "{} ({})",
                    built_info::PKG_VERSION,
                    built_info::GIT_COMMIT_HASH_SHORT
                        .map(|x| x.to_string())
                        .or_else(|| std::env::var("GIT_SHA").ok())
                        .expect("Git commit hash should be available"),
                ),
            ),
        ],
        SCHEMA_URL,
    )
}
