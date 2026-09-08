# Pre-submission audit

Baseline: `7b23a07` (2026-09-08). Review and cleanup are restricted to `restfulemf`.
The publication workflow is inspected as an integration point, not refactored.
Generated build output is excluded from the inventory. Model/image assets are tracked separately from source code.

All 133 baseline inventory entries were reviewed individually. All remaining files received a second review;
the 19 removed assets were individually verified as byte-identical duplicates before removal.
Runtime validation is complete, including Docker and the large-model profile. Checkstyle passes across all 73 Java files,
with ten method-scoped, explained last-resort exceptions covering eleven necessary runtime exception catches.
This audit ledger is one additional deliverable, not part of the baseline inventory.

## File inventory

| File | Category | First pass | Second pass | Disposition |
| --- | --- | --- | --- | --- |
| `.github/workflows/publish-restful-emf-snapshot.yml` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/pom.xml` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/.classpath` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/.project` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/.settings/org.eclipse.core.resources.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/.settings/org.eclipse.jdt.apt.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/.settings/org.eclipse.jdt.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/.settings/org.eclipse.m2e.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/README.md` | build/config/docs | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-client/pom.xml` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-client/src/main/java/org/eclipse/sirius/web/restfulemf/RestfulEMFURIHandler.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-client/src/main/java/org/eclipse/sirius/web/restfulemf/client/RestfulEMFClient.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-client/src/test/java/org/eclipse/sirius/web/restfulemf/RestfulEMFURIHandlerTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-client/src/test/java/org/eclipse/sirius/web/restfulemf/client/RestfulEMFClientTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/.classpath` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.project` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.settings/org.eclipse.core.resources.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.settings/org.eclipse.jdt.apt.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.settings/org.eclipse.jdt.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.settings/org.eclipse.m2e.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.settings/org.springframework.ide.eclipse.boot.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/.settings/org.springframework.ide.eclipse.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/README.md` | build/config/docs | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/pom.xml` | build/config/docs | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/java/org/eclipse/sirius/web/restfulemf/sample/ManyModelsRestEMFDemo.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/java/org/eclipse/sirius/web/restfulemf/sample/SampleApplication.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/java/org/eclipse/sirius/web/restfulemf/sample/configuration/ManyModelsProjectTemplatesInitializer.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/java/org/eclipse/sirius/web/restfulemf/sample/configuration/ManyModelsProjectTemplatesProvider.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/java/org/eclipse/sirius/web/restfulemf/sample/configuration/SampleEMFConfiguration.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse1.ecorebin` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse10.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse11.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse12.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse13.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse14.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse15.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse16.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse17.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse18.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse19.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse2.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse20.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse3.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse4.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse5.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse6.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse7.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse8.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/1Modeling/reverse9.ecorebin` | asset | reviewed | removed; byte-identical duplicate | removed duplicate |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/Big_Guy.flow` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/NobelPrize.bpmn` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/application.properties` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/library.ecore` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/linux-kernel.uml` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/project-templates/1MModeling-Template.png` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/project-templates/Models-Template.png` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/project-templates/Studio-Template.png` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/main/resources/reverse1.ecorebin` | asset | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/AbstractIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/ProjectTemplatesIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFCapabilityIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFClientIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFLargeModelIT.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFPathIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFReadIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFStrictConcurrencyIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFTransferCapacityTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf-sample/src/test/java/org/eclipse/sirius/web/restfulemf/sample/RestfulEMFWriteIntegrationTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/.checkstyle` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.classpath` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.project` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.settings/org.eclipse.core.resources.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.settings/org.eclipse.jdt.apt.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.settings/org.eclipse.jdt.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.settings/org.eclipse.m2e.core.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/.settings/org.springframework.ide.eclipse.prefs` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/README.md` | build/config/docs | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/pom.xml` | build/config/docs | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/GetResourceContentEventHandler.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/GetResourceContentInput.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/GetResourceContentSuccessPayload.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/IResourceSnapshotService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/ReplaceDocumentEventHandler.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/ReplaceResourceContentInput.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/ReplaceResourceContentSuccessPayload.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/ResourceRevisionConflictPayload.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/ResourceSnapshot.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/ResourceSnapshotService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/RestfulEMFPersistenceService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/RestfulEMFReadApplicationService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/RestfulEMFWriteApplicationService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/IResourceWriter.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/IRestfulEMFReadApplicationService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/IRestfulEMFWriteApplicationService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/ResourceFormat.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/ResourceRepresentation.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/ResourceWriteResult.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/ResourceWriteStatus.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/RestfulEMFError.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/application/api/RestfulEMFException.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/configuration/OnRestfulEMFEnabled.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/configuration/RestfulEMFAutoConfiguration.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/configuration/RestfulEMFProperties.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/controllers/RestfulEMFExceptionHandler.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/controllers/RestfulEMFResourceController.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/DetachedResourceSet.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/OrderedXMIResource.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/ProjectDocumentsService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/ResourceFormatService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/ResourcePaths.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/ResourceReferences.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/SizeLimitedInputStream.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/SnapshotEObjectIDManager.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/api/IProjectDocumentsService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/api/IResourceFormatService.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/api/ProjectDocuments.java` | production | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/main/java/org/eclipse/sirius/web/restfulemf/services/api/ResourceDocument.java` | production | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | build/config/docs | reviewed | reviewed | unchanged |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/GetResourceContentEventHandlerTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/ReplaceDocumentEventHandlerTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/ResourceFormatServiceTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/ResourceReferenceIdentityTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/ResourceReferencesTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/ResourceSnapshotServiceTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/ResourceTypedReferenceTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/RestfulEMFPersistenceServiceTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/application/RestfulEMFApplicationServicesTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/configuration/OnRestfulEMFEnabledTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/controllers/RestfulEMFExceptionHandlerTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/controllers/RestfulEMFResourceControllerTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/services/ProjectDocumentsServiceTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/services/ResourcePathsTests.java` | test | reviewed | reviewed | modified |
| `restfulemf/backend/sirius-web-restful-emf/src/test/java/org/eclipse/sirius/web/restfulemf/services/SizeLimitedInputStreamTests.java` | test | reviewed | reviewed | modified |

## Coverage and changes

| Category | Reviewed | Changed or removed | Intentionally unchanged |
| --- | ---: | ---: | ---: |
| build/config/docs | 32 | 5 | 27 |
| production | 46 | 37 | 9 |
| test | 27 | 27 | 0 |
| asset | 28 | 19 | 9 |

Of the 133 baseline files, 69 were modified, 19 binary duplicates removed, and 45 intentionally unchanged.
This ledger is one new file in addition to those counts. No production or test classes were deleted.
Many source changes are required author tags or test-style alignment; they are not presented as functional refactoring.
Eclipse metadata is preserved. The publication workflow was inspected but not modified.

## Review findings and retained design

- The client rejects missing, weak and malformed response ETags instead of accidentally converting a wildcard into an unconditional overwrite.
- Snapshot copying does not demand-load references. Import rejects unresolved containment/root proxies which emfjson cannot persist faithfully.
- Reference reconciliation only mutates writable project documents. User fragments are matched against owned objects and native EMF fragments without resolving arbitrary reference navigation.
- Reload diagnostics now compare normalized URIs against actual outgoing references, whether already resolved or still proxies.
  This fixes the HTTP 500 revealed by the rewritten cross-document integration test; unrelated missing metamodels remain fatal.
- Project lookup uses the upstream UUID parser; persistence failures no longer masquerade as absent projects.
- Removed one-use handler delegation, redundant null validation and request-semaphore fairness which had no effect with tryAcquire.
- The sample reuses one binary fixture for twenty independent loads. Its tests now exercise HTTP mutation and durable reload rather than mutating the live editing context from the test thread.
- Added direct spring-tx and spring-data-jdbc dependencies already used in production. Removed the unused sample Testcontainers Jupiter extension; retained the manually managed PostgreSQL container.
- Existing public interfaces and overloads were searched across the repository and retained where they represent application boundaries or possible downstream contracts. No speculative compatibility break was made.
- Immutable event inputs retain their defensive byte-array/list copies because request data crosses the servlet/collaborative-thread boundary.
- Ten method-scoped Checkstyle suppressions cover eleven runtime catches at ownership-transfer, rollback and error-translation
  boundaries. Each is explained locally. Narrowing those catches loses cleanup/error guarantees; replacing them requires
  success flags or wrappers. No other Checkstyle rule was suppressed, and no global exclusion was added.

Eight behavioral test methods were added. Existing tests were strengthened around real serialization, ETag validation,
containment/opposites, safe fragment resolution, persistence failures, HTTP writes, capacity release and large-model reload.
Fourteen existing behavioral tests were strengthened or rewritten. No test methods were deleted or consolidated;
duplicate fixture-size assertions and internal invocation assertions were removed. Remaining test changes align style or
make fixture resource-ID registration consistent with EMF.

## EMF and integration evidence

Installed Sirius Web 2026.7.3 sources confirm that EditingContextEventProcessor serializes inputs on its dedicated executor,
and EditingContextSaver persists SEMANTIC_CHANGE notifications. The REST handlers use this lifecycle; no command stack,
editing domain, global model cache or additional concurrency framework was introduced.

Reviewed native containment/opposite notifications, non-resolving traversal, intrinsic/extrinsic IDs, resource attachment,
detached serialization, inbound-reference rebinding and rollback. Tests use real EMF models at these boundaries.
The persistence adapter commits before success and delegates non-REST changes; the existing upstream service can silently
omit failed serialization, so simply removing the adapter would weaken the REST durability contract.

Inspected the installed ResourceImpl, BasicEObjectImpl, EModelElementImpl, EcoreUtil, NotifyingListImpl, EObjectIDManager,
JSONResourceFactory and emfjson serialization sources. Compared sample initialization with upstream semantic-data initializers.
ResourceSets remain operation-local or collaborative-context-owned; the client handler is documented as thread-confined.

## Maintainer questions

1. **Persistence integration:** the primary persistence adapter and explicit transaction are intentional for durable HTTP success,
   but applications with another primary implementation must compose them. An upstream extension point would be preferable
   if maintainers choose to support this contract centrally.
2. **JSON limitations:** specialized Ecore object identities and unresolved containment cannot always survive upstream JSON storage.
   Explicit rejection is intentional. Supporting all such references needs an upstream serialization change, not a database workaround.
3. **Assets:** origin and licensing of demonstration models/images are not established by their initial repository commit.
   Maintainer confirmation is required before distributing them upstream.
4. **Deployment:** snapshot dependencies and authenticated GitHub repositories remain; the existing publication workflow does not
   run Docker/sample tests on pull requests. Upstream release/CI placement needs a maintainer decision.
5. **CSV:** preserved flattening omits many-valued attributes and can collide on column names. A richer CSV contract needs an explicit decision.
6. **URI trust:** canonical sirius: references are accepted independently of document membership. No cross-project loader bypass
   was demonstrated; applications supplying custom URI loaders should review authorization.
7. **Timeouts:** Reactor timeout translation does not establish cancellation of an already dispatched model mutation.
   Upstream processing includes blocking work, so the ten-second constant must not be treated as a guaranteed end-to-end deadline.
8. **HTTP parsing:** Spring rejects wholly malformed If-Match but tolerates garbage around otherwise recognizable tags.
   Stricter syntax would be a deliberate boundary-contract change; no custom HTTP parser was introduced during this cleanup.
9. **Rollback:** failures are tested against real references and pending proxies, but arbitrary downstream EMF adapters can have
   external side effects that resource restoration cannot undo. A failed rollback is logged, never presented as success.
10. **Style exceptions:** the necessary broad runtime catches use narrowly scoped last-resort suppressions, not a weakened
    global rule. Maintainers may discuss these boundaries. Twenty-two advisory complexity/coupling/demo warnings remain;
    cohesive code was not fragmented into artificial classes just to lower a metric.

## Validation checkpoints

All commands run with Java 21:
`JAVA_HOME=/home/cedric/bin/java/temurin_21/jdk-21.0.6+7`.

- `mvn -q -f restfulemf/backend/pom.xml verify`: passed after first cleanup.
- `mvn -q -f restfulemf/backend/pom.xml clean verify`: passed after the second review.
  Client: 15 executions; server: 94 executions; zero failures/errors/skips.
  JaCoCo checkpoint: client 216/226 lines (95.58%); server 829/992 lines (83.57%).
- `mvn -q -f restfulemf/backend/pom.xml -Psample test-compile -DskipTests`: passed.
- `git diff --check -- restfulemf`: passed.
- `mvn -f restfulemf/backend/pom.xml -pl sirius-web-restful-emf dependency:analyze-only`: inspected;
  production undeclared dependencies were corrected, intentional test-starter/annotation-processor findings retained.
- `mvn -o -q -f restfulemf/backend/pom.xml -Psample,large-model-tests clean verify`: passed, exit 0 on 2026-09-08.
  15 client + 95 server + 27 sample + 1 large-model executions: **138 passed**, no failures/errors/skips.
  Large-model test duration: 201.28 seconds. Fresh reports were checked by modification time; historical reports were excluded.
  Coverage: client 216/226 lines (95.58%); server 855/1001 lines (85.41%); the 80% gates pass without exclusions.
- The online full build was stopped after a thread dump confirmed blocking remote Maven metadata retrieval.
  Offline validation used the already resolved dependency cache; fresh-machine authenticated resolution remains a deployment concern.
- `mvn -o -q -f restfulemf/backend/pom.xml verify`: passed after the final fixture correction and scoped annotations;
  all 110 client/server executions and both coverage gates pass.
- `mvn -o -f restfulemf/backend/pom.xml -pl sirius-web-restful-emf dependency:analyze-only`: passed;
  no undeclared production dependency remains. Test-starter and annotation-processor warnings were verified as intentional.
- `mvn -o -q -f /tmp/restfulemf-checkstyle.weeCLA/pom.xml checkstyle:check`: **passed, zero errors**.
  A fresh cache run checked all 73 Java files with no missing inventory entry. The temporary audit POM explicitly lists all six
  production/test source roots and uses Maven Checkstyle 3.6.0 with the upstream configuration at
  `/home/cedric/src/sirius-web-alwaysclean/again/sirius-web/packages/releng/backend/sirius-components-resources/checkstyle/CheckstyleConfiguration.xml`.
  The ordinary command-line include-test property does not configure this plugin parameter; explicit source roots were required.
  All hard errors are corrected or covered by the ten explained last-resort method exceptions. Twenty-two advisory warnings
  remain, chiefly complexity/coupling metrics and intentional demo output; each warning was inspected.

## Assessment

The contribution is clearer and safer, but not smaller in Java line count: focused regressions and upstream style alignment
outweigh the deleted wrappers. Nineteen duplicated binary files were removed. No speculative framework or public API break
was introduced. EMF usage is conventional and supported by real-model and HTTP regressions; remaining persistence and
serialization constraints are explicit rather than hidden.

The implementation is suitable for technical submission with this audit: tests and checks pass, the relevant contracts are
protected, and the remaining design constraints are explicit. The persistence adapter and sample asset provenance deserve
upstream discussion before merging/distributing. This is not a claim that every model shape or downstream custom adapter is
supported. No known unnecessary abstraction was retained except existing public convenience APIs whose removal would require
a separate compatibility decision.

Suggested commit message:

```text
Harden RESTful EMF imports and simplify submission fixtures

Preserve cross-document references through JSON reloads and reject model
structures the persistence format cannot retain. Tighten client ETag
validation and protect the contracts with real EMF and HTTP regressions.

Reuse the large-model fixture, align dependencies and source conventions,
and document the exhaustive audit and remaining integration questions.
```
