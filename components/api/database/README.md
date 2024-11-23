<h1 align="center">
  Database
</h1>

<p align="center">
  <a href="https://postgresql.org/"><img alt="PostgreSQL 17" src="https://img.shields.io/badge/PostgreSQL-17-blue"></a>
  <a href="https://www.jetbrains.com/idea/"><img alt="editor: IntelliJ IDEA Ultimate" src="https://img.shields.io/badge/editor-IntelliJ_IDEA_Ultimate-black"></a>
  <a href="#contributors"><img alt="Contributors" src="https://img.shields.io/github/contributors/dansdata-se/dansdata?color=0e0c33" /></a>
  <a href="./LICENSE"><img src="https://img.shields.io/badge/license-MIT-green.svg" alt="MIT License" /></a>
</a>

<p align="center">
  Primary database for Dansdata's backend services.
</p>

## Migrations

We have not yet decided on a migration tool.

Please update [`migrate.sh`](migrate.sh) to generate necessary SQL files to migrate from a zero state.

## Development Environment

Open this directory in IntelliJ IDEA Ultimate.

### Plugins

These editor plugins should be installed for the smoothest developer experience.

| Plugin                              | Motivation                      | url                                                                                        |
| ----------------------------------- | ------------------------------- | ------------------------------------------------------------------------------------------ |
| Database Tools and SQL for WebStorm | SQL language support            | (bundled) <https://plugins.jetbrains.com/plugin/10925-database-tools-and-sql-for-webstorm> |
| detekt                              | Static code analysis for Kotlin | <https://plugins.jetbrains.com/plugin/10761-detekt>                                        |
| docker                              | Local testing                   | (bundled) <https://plugins.jetbrains.com/plugin/7724-docker>                               |
| ktfmt                               | Kotlin code formatter           | <https://plugins.jetbrains.com/plugin/14912-ktfmt>                                         |

## Running Tests

_Docker must be installed on your system to run database tests!_

From the `tests` subdirectory, run

```bash
./gradlew :check
```
