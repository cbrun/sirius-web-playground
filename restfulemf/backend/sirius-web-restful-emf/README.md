# RESTful EMF for Sirius Web

This prototype exposes Sirius Web semantic documents as XMI, zipped XMI, EMF binary, or CSV. It targets Sirius Web 2026.7.3 and preserves EMF object identifiers when documents are downloaded and uploaded again.

The project path parameter is always the Sirius Web project ID. A document can be selected by its UUID or its exact name.

## Integration and build

Add the `org.eclipse.sirius:sirius-web-restful-emf:2026.7.3-SNAPSHOT` dependency to a Sirius Web application, then enable
the component with `sirius.web.enabled=restful-emf` (or `sirius.web.enabled=*`). An explicit
`sirius.web.disabled=restful-emf` takes precedence.

Build the reusable component from the repository root:

```shell
mvn clean verify -f restfulemf/backend/pom.xml
```

## Endpoints

All endpoints start with `/api/rest/projects/{projectId}`.

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/documents` | Lists document UUIDs and names. |
| `GET`, `PUT` | `/{document}/xmi` | Reads or replaces a document as XMI. |
| `GET`, `PUT` | `/{document}/xmi.zip` | Reads or replaces a document as zipped XMI. |
| `GET`, `PUT` | `/{document}/bin` | Reads or replaces a document using EMF binary serialization. |
| `GET` | `/{document}/csv?sep=\t` | Exports mono-valued attributes as CSV. |
| `GET` | `/epackages/bin` | Returns the registered EPackages as an EMF binary resource. |

Unknown projects, documents, or resources return `404`. Invalid XMI or binary input returns `400`, stale updates return `412`, and updates to read-only documents return `403`. Oversized uploads return `413`. CSV updates are not supported and return `405`; exhausted transfer capacity returns `503` with `Retry-After: 1`, and collaborative event timeouts return `504`. Error responses use `application/problem+json` and expose a stable `code` property.

Resource responses include an `ETag` computed from the canonical Sirius Web JSON content. A PUT carrying `If-Match` is applied only if that revision is still current. For backward compatibility, a PUT without `If-Match` remains accepted by default. Set `sirius.web.restful-emf.require-if-match=true` to reject such requests with `428 Precondition Required`.

An EMF client can use the binary endpoint directly:

```java
Map<String, Object> options = new HashMap<>();
options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);

URI uri = URI.createURI("http://localhost:8080/api/rest/projects/PROJECT_ID/DOCUMENT_ID/bin");
ResourceSet resourceSet = new ResourceSetImpl();
resourceSet.getURIConverter().getURIHandlers().add(0, new RestfulEMFURIHandler());
resourceSet.getPackageRegistry().put(FlowPackage.eNS_URI, FlowPackage.eINSTANCE);
Resource resource = new XMLResourceImpl(uri);
resourceSet.getResources().add(resource);
resource.load(options);

// Modify the model, then persist it back to Sirius Web.
resource.save(options);
```

`RestfulEMFURIHandler` remembers the `ETag` received by `load()` and sends it as `If-Match` during `save()`. Consequently, `Resource.save()` throws an `IOException` instead of overwriting a concurrent update.

## Large transfers

Document downloads and uploads use synchronous servlet streams. XMI and binary serializers write directly to the HTTP
response, and CSV export discovers columns in a first traversal before writing rows in a second traversal. Document
format conversion moves the temporary snapshot contents instead of copying the complete EMF graph. The canonical Sirius
Web JSON snapshot remains materialized because it is the persistence and collaborative-event contract.

The following properties bound memory exposure. Values use Spring `DataSize` syntax such as `256MB` or `1GB`:

| Property | Default | Purpose |
| --- | --- | --- |
| `sirius.web.restful-emf.max-request-size` | `256MB` | Maximum number of bytes read from a PUT body. |
| `sirius.web.restful-emf.max-uncompressed-size` | `256MB` | Maximum uncompressed size of a zipped XMI upload. |
| `sirius.web.restful-emf.max-concurrent-transfers` | `2` | Concurrent streamed downloads and uploads accepted by one application instance. |
| `sirius.web.restful-emf.require-if-match` | `false` | Whether PUT requests without `If-Match` are rejected. |

The concurrency limit is local to each application instance. A reverse proxy should enforce the corresponding global
policy when several instances are deployed.

## Architecture

The REST controller only handles HTTP parameters, capability checks and response mapping. Read and write application
services resolve projects and documents, then dispatch access to the collaborative editing context. The live EMF
`ResourceSet` is only read or modified by collaborative handlers. A dedicated format service owns XMI, binary and CSV
conversion, while Sirius Web domain objects remain behind the project-document service boundary.

## Status and security

This is a playground prototype, not a production-ready API. Access to project resources follows the Sirius Web `VIEW` and `EDIT` capabilities, but the sample application does not configure an authentication mechanism. Put it behind appropriate authentication before exposing it outside a development environment.

CSV currently ignores multi-valued attributes and only supports reads.
