#################################################
# Basic just configuration
#################################################

export GIT_SHA := `git rev-parse HEAD`
export GIT_REF := `git rev-parse --abbrev-ref HEAD`

mod backstage "components/api/backstage/_.justfile"

[private]
default: help

# Show this help text
[group("General")]
help:
    @just --list --unsorted --justfile {{ justfile() }}

# Execute the given command in the context of 'just', providing access to envvars etc.
[group("General")]
run cmd:
    {{ cmd }}

#################################################
# Code Utils
#################################################

# Reformat all source files
[group("Code Utils")]
format:
    @npx prettier -w .
    @just --unstable --fmt
    @find . -name 'node_modules' -prune -false -o -name '*.justfile' -exec just --unstable --fmt --justfile {} \;

#################################################
# Repository Management
#################################################

# Initialize git hooks etc.
[group("Repository Management")]
init_repo npm_install="1":
    @{{ if npm_install == "1" { "npm install" } else { "" } }}
    @npx husky
