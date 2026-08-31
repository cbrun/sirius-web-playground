# RESTful EMF sample application

The sample runs Sirius Web 2026.7.3 on Java 21 with the Ecore, Flow, BPMN and UML metamodels enabled.
It keeps the standard Blank, Studio, Blank Studio and Flow project templates, plus the playground's Many Models and
1M-Modeling templates.

Start PostgreSQL:

```shell
docker run --rm --name playground-postgres \
  -p 5433:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -d postgres:17-alpine
```

Then start the application from the repository root:

```shell
mvn -f restfulemf/backend/pom.xml -Psample -pl sirius-web-restful-emf-sample -am spring-boot:run
```

The application is available at `http://localhost:8080`.

## Run the REST EMF client demo

With the application running, create and open a **Many Models** project in Sirius Web. Run the
`ManyModelsRestEMFDemo.main` method from the IDE, then paste the URL of the opened project when prompted.

The demo uses an EMF `Resource` backed by the remote binary URI. It loads `linux-kernel.uml`, adds a package named
`Created from The Client code` at the first position of the UML model, pauses, saves it, and reloads it to verify the
change. Press Enter immediately to test a successful save, or modify the model in Sirius Web during the pause to test
the stale-update rejection.

Run the full build and the PostgreSQL-backed integration tests with a running Docker daemon:

```shell
mvn clean verify -Psample -f restfulemf/backend/pom.xml
```

Run the opt-in 1M-Modeling round-trip with a 4 GiB test JVM:

```shell
mvn clean verify -Psample,large-model-tests -f restfulemf/backend/pom.xml
```

The tests use the Sirius Web Ecore and Flow fixtures, cover compatible and strict optimistic concurrency, rollback and
cross-resource references, exercise creation of the mixed-format Many Models template, verify the 1M-Modeling assets,
enforce streamed-transfer limits, and start an isolated PostgreSQL 17 container through Testcontainers. The large-model
profile additionally creates the full 1M-Modeling project and performs an EMF binary load/save round-trip.
