# RESTful EMF for Sirius Web

This prototype exposes Sirius Web semantic documents as XMI, zipped XMI, EMF binary, or CSV. It targets Sirius Web 2026.7.3 and preserves EMF object identifiers when documents are downloaded and uploaded again.

The project path parameter is always the Sirius Web project ID. Document names are relative paths, such as
`domain/customer.ecore`. This uses the existing document name: no database migration, table, or hidden document is needed.

## Integration and build

Snapshots built from the `master` branch are published to GitHub Packages. GitHub requires authentication to download
public Maven packages: create a classic personal access token with the `read:packages` scope, then expose the associated
GitHub account and token as `GITHUB_USER` and `GITHUB_TOKEN`.

Configure both package repositories in the consuming project:

```xml
<repositories>
    <repository>
        <id>github-restful-emf</id>
        <url>https://maven.pkg.github.com/cbrun/sirius-web-playground</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>github-sirius-web</id>
        <url>https://maven.pkg.github.com/eclipse-sirius/sirius-web</url>
    </repository>
</repositories>
```

Add matching credentials to `~/.m2/settings.xml`. Do not store the token in the project POM:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">
    <servers>
        <server>
            <id>github-restful-emf</id>
            <username>${env.GITHUB_USER}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
        <server>
            <id>github-sirius-web</id>
            <username>${env.GITHUB_USER}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
</settings>
```

Add the component dependency to the Sirius Web application:

```xml
<dependency>
    <groupId>org.eclipse.sirius</groupId>
    <artifactId>sirius-web-restful-emf</artifactId>
    <version>2026.7.3-SNAPSHOT</version>
</dependency>
```

Enable the component with `sirius.web.enabled=restful-emf` (or `sirius.web.enabled=*`). An explicit
`sirius.web.disabled=restful-emf` takes precedence.

Build the reusable server and standalone client from the repository root with Java 21:

```shell
mvn clean verify -f restfulemf/backend/pom.xml
```

Use `mvn clean install -f restfulemf/backend/pom.xml` to consume the artifacts locally. Build and run the sample integration
tests with `mvn clean verify -Psample -f restfulemf/backend/pom.xml`; these tests require a running Docker daemon.
Eclipse project metadata is supplied for importing the Maven modules into the IDE.

`verify` enforces at least **80% line coverage** independently for the reusable server and client modules using JaCoCo,
with no class or package exclusions. This check also runs before the GitHub Actions snapshot deployment. HTML reports
are generated in each module's `target/site/jacoco/index.html`. The sample application is an integration-test harness,
not part of this coverage gate; the reusable modules meet the threshold with their own tests, without requiring Docker.

## Endpoints

All endpoints start with `/api/rest/projects/{projectId}`.

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/documents` | Lists `{id, name, path, readOnly}` entries, sorted by path. |
| `GET`, `HEAD`, `PUT` | `/documents/xmi/{path}` | Reads, creates or replaces a document as XMI. |
| `GET`, `HEAD`, `PUT` | `/documents/xmi.zip/{path}` | Reads, creates or replaces zipped XMI. |
| `GET`, `HEAD`, `PUT` | `/documents/bin/{path}` | Reads, creates or replaces EMF binary. |
| `GET`, `HEAD` | `/documents/csv/{path}?sep=\t` | Exports mono-valued attributes as CSV. |
| `GET`, `HEAD` | `/epackages/{format}` | Returns registered EPackages as `xmi` or `bin`. |

Paths are case-sensitive and may contain directories, spaces and Unicode characters. Encode each path segment when
constructing a URL. Empty segments, `.` and `..` segments, backslashes and ambiguous encodings are rejected. Directories
are virtual: no directory-creation request is needed. Relative model references such as `../common/types.ecore#//Address`
are supported within the project's document space; placing the format before the path preserves standard URI resolution.

Existing documents with invalid or duplicate names are listed under `_by-id/{uuid}`. These reserved fallback paths support
reads and replacement of existing documents only. An ambiguous name returns `409`, never an arbitrary matching document.
Renaming a document changes its public path. Already bound references retain their internal document UUID, while external
URLs and unresolved references using the old path must be updated by their callers.

PUT returns `201 Created` with `Location` for a new document, or `204 No Content` for replacement. It does not create
projects. Uploads are independent operations, not a batch transaction. This API does not expose DELETE.

Unknown projects, documents, or resources return `404`. Invalid XMI or binary input returns `400`, stale updates return `412`, and updates to read-only documents return `403`. Oversized uploads return `413`. CSV updates are not supported and return `405`; exhausted transfer capacity returns `503` with `Retry-After: 1`, and collaborative event timeouts return `504`. Error responses use `application/problem+json` and expose a stable `code` property.

GET and HEAD include a strong `ETag` for the served representation. A PUT carrying `If-Match` replaces only that revision;
`If-None-Match: *` creates only when absent. Preconditions are checked on the editing-context thread together with the
write. Unconditional PUT creates or replaces by default, allowing native EMF HTTP use. Set
`sirius.web.restful-emf.require-if-match=true` to require one of these conditions (`428` when missing, `412` when false).
Use the ETag obtained from the same format as the PUT. Successful PUT responses do not include an ETag because the server
transforms the received representation into Sirius Web JSON. Reload before the next protected save.

This is a breaking route and listing change: replace `/{document}/xmi` with `/documents/xmi/{path}` (likewise for the other
formats), and replace UUID-to-name listing maps with the entry array. Consumers should obtain paths from the listing.

## Import a local directory with native EMF

No RESTful EMF client dependency or separate document-creation request is required. Register the appropriate resource
factories and generated metamodel packages in an ordinary ResourceSet, load the local files, then map the directory URI:

```java
Path directory = Path.of("models").toAbsolutePath();
ResourceSet resources = new ResourceSetImpl();
resources.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
resources.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
List<Resource> imported;
try (var files = Files.walk(directory)) {
    imported = files.filter(Files::isRegularFile)
            .filter(file -> file.toString().endsWith(".ecore"))
            .sorted()
            .map(file -> resources.getResource(URI.createFileURI(file.toString()), true))
            .toList();
}
URI localRoot = URI.createFileURI(directory.toString()).appendSegment("");
URI remoteRoot = URI.createURI("http://localhost:8080/api/rest/projects/PROJECT_ID/documents/xmi/");
resources.getURIConverter().getURIMap().put(localRoot, remoteRoot);
for (Resource resource : imported) {
    resource.save(Map.of());
}
```

Imports use `java.nio.file.Files`, `java.nio.file.Path`, `java.util.List`, `java.util.Map`, EMF `URI`, `Resource`,
`ResourceSet`, `ResourceSetImpl`, `EcorePackage`, and `EcoreResourceFactoryImpl`. For other model types, register their
factory and packages instead. Binary input must be loaded with its binary options before exporting as XMI.
Upload order is unrestricted for non-containment references: unresolved references retain their relative path and original fragment in normal JSON
content and are reconciled when their target arrives, including after a restart. Cycles do not need special client code.
Original non-UUID IDs are mapped deterministically; semantic intrinsic-ID attributes are not rewritten. Original IDs
already discarded by historical non-REST imports cannot be recovered.

Imported external references must target a registered metamodel namespace or an already loaded server resource;
unknown HTTP, file and pathmap dependencies are rejected instead of being fetched by the server. Pending references
in read-only documents are not rewritten when another document arrives. Zipped XMI uploads must contain exactly one file.
Unresolved containment references and proxy roots are rejected with HTTP 400: the upstream JSON persistence format
cannot preserve them. Contained objects must be included in the uploaded document.

For authenticated, revision-protected use, install the optional `RestfulEMFURIHandler` as documented below. Plain EMF
does not automatically send ETags or authentication headers.

## Standalone EMF client

Client applications should depend on `org.eclipse.sirius:sirius-web-restful-emf-client:2026.7.3-SNAPSHOT`, not this server artifact. See the [client README](../sirius-web-restful-emf-client/README.md) for Maven setup, authentication, generated metamodels, and standalone handler usage.

Load all semantic documents from a project or any of its sub-URLs:

```java
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sirius.web.restfulemf.client.RestfulEMFClient;

ResourceSet resources = new RestfulEMFClient().loadProject(
        URI.createURI("http://localhost:8080/projects/PROJECT_ID/edit"));
```

The client installs `RestfulEMFURIHandler`, which remembers the `ETag` received by `load()` and sends it as `If-Match` during
`save()`. A stale save throws an `IOException`. After a successful transformed PUT, reload the resource before another
protected save; the handler does not silently adopt a potentially newer revision with an extra HEAD request. Project
loading includes semantic documents, not representations or metadata, and is not an atomic project snapshot.

## Ecore identity limitation

The upstream EMF JSON format does not preserve identifiers for Ecore annotations, operations, parameters, generic types,
type parameters, enum literals, nested packages, or annotation detail entries. These objects may be contained in uploaded
models, but persisted non-containment references targeting them are not supported. When the target can be identified,
the import rejects the reference with HTTP 400 instead of accepting a reference that would become dangling after reload.
This check also applies when a later upload resolves the target of a pending reference. A generic unresolved proxy using
an identifier already lost by the upstream format cannot be reliably identified and may remain unresolved. References to
registered external metamodel objects are not subject to this document-storage restriction.

For downloads, identifiers omitted by the stored JSON receive deterministic, document-scoped structural-fragment IDs in
the detached snapshot. This makes representation ETags repeatable; it does not recover identifiers already lost by the
upstream format. Full support for references to these Ecore objects requires an upstream serialization fix.

## Large transfers

Document downloads use synchronous servlet streams. PUT bodies are buffered up to the configured request limit before
dispatch, so their lifetime does not depend on the servlet request while the editing context processes them. XMI and binary serializers write directly to the HTTP
response, after a streaming digest pass over the immutable snapshot to compute the representation ETag. CSV export
discovers columns in a first traversal before writing rows in a second traversal. Document
format conversion moves the temporary snapshot contents instead of copying the complete EMF graph. The canonical Sirius
Web JSON snapshot remains materialized because it is the persistence and collaborative-event contract.

The following properties bound memory exposure. Values use Spring `DataSize` syntax such as `256MB` or `1GB`:

| Property | Default | Purpose |
| --- | --- | --- |
| `sirius.web.restful-emf.max-request-size` | `256MB` | Maximum number of bytes read from a PUT body. |
| `sirius.web.restful-emf.max-uncompressed-size` | `256MB` | Maximum uncompressed size of a zipped XMI upload. |
| `sirius.web.restful-emf.max-concurrent-transfers` | `2` | Concurrent streamed downloads and uploads accepted by one application instance. |
| `sirius.web.restful-emf.require-if-match` | `false` | Require `If-Match` for replacement or `If-None-Match: *` for creation. |

The concurrency limit is local to each application instance. A reverse proxy should enforce the corresponding global
policy when several instances are deployed.

The collaborative timeout is not an end-to-end request deadline: dispatch may block before the response publisher is
returned. A client or proxy timeout does not cancel a dispatched write. Reload before retrying when its outcome is unknown.

## Architecture

The REST controller only handles HTTP parameters, capability checks and response mapping. Read and write application
services resolve projects and documents, then dispatch access to the collaborative editing context. The live EMF
`ResourceSet` is only read or modified by collaborative handlers. A dedicated format service owns XMI, binary and CSV
conversion, while Sirius Web domain objects remain behind the project-document service boundary.

REST writes commit through the existing document domain service before returning success. The component supplies a
primary editing-context persistence service that delegates non-REST changes to the standard Sirius Web service and
avoids persisting REST changes twice. Applications with their own primary persistence service must explicitly compose
these behaviors instead of registering two primary implementations.

## Status and security

This is a playground prototype, not a production-ready API. Access to project resources follows the Sirius Web `VIEW` and `EDIT` capabilities, but the sample application does not configure an authentication mechanism. Put it behind appropriate authentication before exposing it outside a development environment.

CSV currently ignores multi-valued attributes and only supports reads.
