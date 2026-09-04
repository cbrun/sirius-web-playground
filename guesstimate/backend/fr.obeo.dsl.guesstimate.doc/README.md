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

`Variable.settings` is the persistent source of truth for a variable's kind and parameters. `Variable.type` is a derived, writable convenience feature: selecting another type replaces the settings object with the matching subtype and its defaults, intentionally discarding the previous parameters.

Variable names are the identifiers used by formulas. They follow the PetitParser grammar: a letter followed by zero or more letters or digits, with case-sensitive matching. Names must be unique within a sheet; EMF validation reports duplicates, and the runtime excludes an ambiguous name instead of silently selecting one variable.

## Formula dependency edges

The variables diagram displays formula dependencies from each input variable to the calculated variable. The edge label shows the operator path through the formula, with the root operator determining its style:

- `+`: green, solid;
- `-`: red, dashed;
- `*`: blue, dotted;
- `/`: orange, dash-dot;
- `^`: purple, solid and thicker.

Nested formulas keep their complete operator path. For example, `C = A * (B + D)` displays `*` for `A` and `* +` for `B` and `D`. Distinct paths for the same input are separated by ` | `.

An edge explains how an input contributes to the calculated variable, from the outermost operation to the innermost one. The first operator in its label determines the edge style; the remaining operators describe the nested branch. For a variable whose formula is `D - E * C`, the precedence rules interpret the expression as `D - (E * C)`: the edge from `D` is labeled `+` because `D` is the positive, left-hand term of the subtraction, while the edges from `E` and `C` are labeled `- *` because both belong to the subtracted term and are multiplied inside it. Likewise, with `A / B`, the edge from `A` is labeled `*` (the multiplicative numerator) and the edge from `B` is labeled `/` (the divisor). This convention makes every label a compact path through the expression tree instead of showing only the nearest operator.

## Formula diagnostics

Syntax errors are reported by EMF validation on the `FormulaSetting.formula` feature with their character position. Runtime failures, such as an input whose sample is unavailable, do not create model diagnostics: they leave the calculated sample empty and are exposed to the simulation code as a typed failure category.
