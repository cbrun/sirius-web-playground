# RESTful EMF for Sirius Web

This prototype exposes Sirius Web semantic documents as XMI, zipped XMI, EMF binary, or CSV. It targets Sirius Web 2026.7.3 and preserves EMF object identifiers when documents are downloaded and uploaded again.

The project path parameter is always the Sirius Web project ID. A document can be selected by its UUID or its exact name.

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

Unknown projects, documents, or resources return `404`. Invalid XMI or binary input returns `400`, and updates to read-only documents return `403`. CSV updates are not supported and return `405`.

An EMF client can use the binary endpoint directly:

```java
Map<String, Object> options = new HashMap<>();
options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);

URI uri = URI.createURI("http://localhost:8080/api/rest/projects/PROJECT_ID/DOCUMENT_ID/bin");
Resource resource = new XMLResourceImpl(uri);
resource.load(options);

// Modify the model, then persist it back to Sirius Web.
resource.save(options);
```

## Status and security

This is a playground prototype, not a production-ready API. It deliberately has no authentication or authorization layer: every project and document available to the application is accessible to unauthenticated callers. Put it behind appropriate access control before exposing it outside a development environment.

CSV currently ignores multi-valued attributes and only supports reads.
