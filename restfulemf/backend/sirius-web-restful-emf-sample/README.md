# RESTful EMF sample application

The sample runs Sirius Web 2026.7.3 on Java 21 with the Ecore, Flow, BPMN and UML metamodels enabled.
It keeps the standard Blank, Studio, Blank Studio and Flow project templates, plus the playground's Many Models and
1M-Modeling templates.

The 1M-Modeling template is a synthetic workload: it loads the same bundled binary model twenty times into
independent documents named `reverse1.ecorebin` through `reverse20.ecorebin`, each with its own identifiers.

Start PostgreSQL:

```shell
docker run --rm --name playground-postgres \
  -p 5433:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -d postgres:17-alpine
```

Then start the application from the repository root:

```shell
mvn -f restfulemf/backend/pom.xml -Psample install
mvn -f restfulemf/backend/sirius-web-restful-emf-sample/pom.xml spring-boot:run
```

The application is available at `http://localhost:8080`.

## Run the REST EMF client demo

With the application running, create and open a **Many Models** project in Sirius Web. Run the
`ManyModelsRestEMFDemo.main` method from the IDE, then paste the URL of the opened project when prompted.

The demo uses the [standalone Maven client](../sirius-web-restful-emf-client/README.md) to load all project documents into an EMF `ResourceSet`, registering the generated UML package first. It selects the first document containing a UML `Model` root (`linux-kernel.uml` in the Many Models template), adds a package named
`Created from The Client code` at the first position of the UML model, pauses, saves that document, and reloads it to verify the
change. Press Enter immediately to test a successful save, or modify the model in Sirius Web during the pause to test
the stale-update rejection.

Resource URIs use the public `/api/rest/projects/PROJECT_ID/documents/bin/PATH` endpoints. Saving and reloading use the client's default binary options and ETag protection. A successful save must be followed by a reload before editing and saving again, since transformed PUT responses carry no ETag. Other project documents are loaded but are not saved by the demo.

Run the full build and the PostgreSQL-backed integration tests with a running Docker daemon:

```shell
mvn clean verify -Psample -f restfulemf/backend/pom.xml
```

Run the opt-in 1M-Modeling round-trip with a 4 GiB test JVM:

```shell
mvn clean verify -Psample,large-model-tests -f restfulemf/backend/pom.xml
```

The tests use the Sirius Web Ecore and Flow fixtures, cover compatible and strict optimistic concurrency, rollback,
native EMF directory imports, cyclic cross-resource references across editing-context reloads, concurrent creation and
representation ETags. They exercise creation of the mixed-format Many Models template,
enforce streamed-transfer limits, and start an isolated PostgreSQL 17 container through Testcontainers. The large-model
profile additionally creates the full 1M-Modeling project, modifies a document through EMF binary load/save,
and reloads the editing context to check that the change was persisted.
