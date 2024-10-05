#################################################
# Basic just configuration
#################################################

[private]
default: help

# Show this help text
[group("General")]
help:
    @just --list backstage --unsorted --justfile {{ justfile() }}

#################################################
# Code Utils
#################################################

# Reformat all source files
[group("Code Utils")]
format:
    @./gradlew ktfmtFormat
    @just --unstable --fmt
