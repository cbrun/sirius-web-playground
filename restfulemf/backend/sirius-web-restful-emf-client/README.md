# RESTful EMF client

A standalone Java 21 library for loading Sirius Web semantic projects into an EMF `ResourceSet` and saving documents with ETag/`If-Match` protection. Runtime dependencies are EMF and Jackson Core; no Spring runtime or Sirius Web server libraries are required. The server must expose the [RESTful EMF endpoints](../sirius-web-restful-emf/README.md).

## Maven dependency

Snapshots from this repository's `master` branch are published to GitHub Packages:

```xml
<dependency>
    <groupId>org.eclipse.sirius</groupId>
    <artifactId>sirius-web-restful-emf-client</artifactId>
    <version>2026.7.3-SNAPSHOT</version>
</dependency>
```

Configure these repositories in your project POM. The second repository resolves the inherited Sirius Web parent POM, not a server runtime dependency:

```xml
<repositories>
    <repository>
        <id>github-restful-emf</id>
        <url>https://maven.pkg.github.com/cbrun/sirius-web-playground</url>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
    <repository>
        <id>github-sirius-web</id>
        <url>https://maven.pkg.github.com/eclipse-sirius/sirius-web</url>
    </repository>
</repositories>
```

GitHub requires authentication even for public Maven packages. Create a classic personal access token with `read:packages`, expose its account and token as `GITHUB_USER` and `GITHUB_TOKEN`, and add matching servers to your Maven `settings.xml` (normally `~/.m2/settings.xml`). Merge this into existing settings; never commit tokens:

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

## Load a complete project

```java
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.sirius.web.restfulemf.client.RestfulEMFClient;

ResourceSet resources = new RestfulEMFClient().loadProject(
        URI.createURI("http://localhost:8080/projects/PROJECT_ID/edit"));
```

Project URLs, workbench sub-URLs, and REST URLs such as `https://example.org/sirius/api/rest/projects/PROJECT_ID/DOCUMENT_ID/bin` identify the same project. Deployment context paths are retained; query strings and fragments are ignored. The URL must identify a project and use HTTP or HTTPS.

The client discovers all documents and downloads them in document-ID order using EMF binary serialization. Resources retain canonical `sirius:///DOCUMENT_ID` URIs, mapped to their HTTP endpoints through the URI converter, so cross-document references and object IDs survive round trips. The returned set contains document resources only.

Registered metamodels are downloaded automatically from `/epackages/bin`. To use generated Java model types, register their packages in an initially empty ResourceSet before loading:

```java
ResourceSet resources = new org.eclipse.emf.ecore.resource.impl.ResourceSetImpl();
resources.getPackageRegistry().put(MyPackage.eNS_URI, MyPackage.eINSTANCE);
new RestfulEMFClient().loadProject(projectURI, resources);
```

Replace `MyPackage` with your generated package. Existing package registrations take precedence over downloaded packages; the global package registry is not modified. ResourceSet load options can also be configured before loading. A populated ResourceSet is rejected; failed loads remove the state added by the client.

## Save and handle concurrent changes

```java
import java.util.Map;
import org.eclipse.emf.ecore.resource.Resource;

Resource document = resources.getResource(URI.createURI("sirius:///DOCUMENT_ID"), false);
// Modify document.getContents() using your generated or reflective EMF API.
document.save(Map.of());

// Reload only after deciding to discard local changes:
document.unload();
document.load(Map.of());
```

Binary load/save options are configured by the client. Keep the installed handler and URI mappings for subsequent loads and saves. The handler remembers each GET's ETag, supplies `If-Match` on PUT, and refreshes it after a successful save. A concurrent update produces an `IOException` reporting HTTP `412`; the client never retries or overwrites automatically. Preserve local work and explicitly reconcile it with the latest server revision before saving again. Other HTTP failures also surface as `IOException`.

## Authentication and standalone handler

Supply application authentication separately from Maven credentials:

```java
RestfulEMFClient client = new RestfulEMFClient(
        Map.of("Authorization", "Bearer " + accessToken));
ResourceSet resources = client.loadProject(projectURI);
```

Headers are used for discovery, downloads, and saves within that project's REST endpoint. Redirects are rejected rather than forwarding credentials. Use HTTPS outside local development.

For an existing EMF application that only needs ETag-aware transport, install the handler directly:

```java
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.web.restfulemf.RestfulEMFURIHandler;

ResourceSet resources = new ResourceSetImpl();
URI endpoint = URI.createURI("http://localhost:8080/api/rest/projects/PROJECT_ID");
resources.getURIConverter().getURIHandlers().add(0,
        new RestfulEMFURIHandler(endpoint, Map.of()));
// Register the document's generated or dynamic EPackages here.
Resource document = new XMLResourceImpl(endpoint.appendSegment("DOCUMENT_ID").appendSegment("bin"));
resources.getResources().add(document);
Map<String, Object> options = Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE);
document.load(options);
// Modify the model.
document.save(options);
```

The existing no-argument handler constructor remains available for unauthenticated use. Its package name is unchanged, but the class now belongs to the **client artifact**: consumers previously depending on the server artifact for this class must add the client dependency. Direct handler usage does not discover metamodels or install project-wide cross-resource URI mappings; use `loadProject` for that.

## Boundaries and build

“Complete project” means the semantic documents exposed by `/documents`, not diagrams, other representations, project metadata, or external libraries. Metamodels must be available from the package endpoint or caller registrations. References resolve lazily; the loader does not eagerly fetch external resources. Loading several documents is not an atomic snapshot, and saving several documents is not a project-wide transaction. Loading a large project requires enough memory for all its documents.

From the repository root, using Java 21 and the Maven authentication above:

```shell
# Client and its parent only; tests need no Docker.
mvn clean verify -f restfulemf/backend/pom.xml -pl sirius-web-restful-emf-client -am

# Default reactor: parent, client, and server.
mvn clean verify -f restfulemf/backend/pom.xml

# Install locally for another Maven project (without waiting for publication).
mvn clean install -f restfulemf/backend/pom.xml -pl sirius-web-restful-emf-client -am

# Include the sample and PostgreSQL-backed integration tests; Docker is required.
mvn clean verify -f restfulemf/backend/pom.xml -Psample
```

See the [sample README](../sirius-web-restful-emf-sample/README.md) for the runnable UML load/edit/save demo. The snapshot workflow publishes the client together with the parent and server artifacts; it does not publish the sample application.
