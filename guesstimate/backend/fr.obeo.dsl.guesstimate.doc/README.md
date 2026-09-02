# Guesstimate

Guesstimate is an EMF-based probabilistic estimation modeler built with Sirius Web 2026.7.3.

## Projects

The runtime modules have a one-way dependency flow:

`app` → `configuration` → `views` → `model`

- `fr.obeo.dsl.guesstimate` contains the EMF model and domain services.
- `fr.obeo.dsl.guesstimate.edit` contains the generated EMF item providers and icons.
- `fr.obeo.dsl.guesstimate.views` builds the Sirius View definitions and packages their images.
- `fr.obeo.dsl.guesstimate.configuration` integrates the model and views with Spring Boot and Sirius Web.
- `fr.obeo.dsl.guesstimate.app` contains the application entrypoint, runtime configuration, and executable packaging.
- `fr.obeo.dsl.guesstimate.releng` owns the parent build and container tooling.
- `fr.obeo.dsl.guesstimate.doc` owns project documentation.

## Prerequisites

- Java 21
- Maven 3.6.3 or newer
- Docker, for the PostgreSQL database and containerized application
- A GitHub token with `read:packages` access for the Sirius Web and Sirius EMF JSON Maven repositories

Configure Maven servers named `github-sirius-web` and `github-sirius-emfjson` in `~/.m2/settings.xml`.

## Build

From the `guesstimate` directory:

```shell
mvn clean verify -f backend/fr.obeo.dsl.guesstimate.releng/pom.xml
```

The executable application is generated in `backend/fr.obeo.dsl.guesstimate.app/target`.

## Run with Docker Compose

Build the application first, then run:

```shell
cd backend/fr.obeo.dsl.guesstimate.releng
docker compose up --build
```

Open <http://localhost:8080>. PostgreSQL is exposed on port 5433 for local development.

## Run from the IDE

From `backend/fr.obeo.dsl.guesstimate.releng`, start only the database with `docker compose up database`, then run `GuesstimateApplication` with the `dev` Spring profile.

The modeler demonstrates programmatic diagram and details-view descriptions, AQL Java services, EMF validation, conditional styles, formula parsing, and probability-distribution sampling.
