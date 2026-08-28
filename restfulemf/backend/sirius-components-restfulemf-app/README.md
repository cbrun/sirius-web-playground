# RESTful EMF sample application

The sample runs Sirius Web 2026.7.3 on Java 21 with the Ecore, Flow, BPMN and UML metamodels enabled.
It keeps the standard Blank, Studio and Flow project templates, plus the playground's Many Models and 1M-Modeling templates.

Start PostgreSQL:

```shell
docker run --rm --name playground-postgres \
  -p 5433:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -d postgres:17-alpine
```

Then start the application from the repository root:

```shell
mvn -f restfulemf/backend/pom.xml -pl sirius-components-restfulemf-app -am spring-boot:run
```

The application is available at `http://localhost:8080`.

Run the full build and the PostgreSQL-backed integration tests with a running Docker daemon:

```shell
mvn clean verify -f restfulemf/backend/pom.xml
```

The tests use the Sirius Web Ecore and Flow fixtures, exercise creation of the mixed-format Many Models template, verify the 1M-Modeling assets, and start an isolated PostgreSQL 17 container through Testcontainers.
