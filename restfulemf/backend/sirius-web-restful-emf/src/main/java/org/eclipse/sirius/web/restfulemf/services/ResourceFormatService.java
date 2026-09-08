/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.restfulemf.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import org.eclipse.sirius.web.restfulemf.IResourceSnapshotService;
import org.eclipse.sirius.web.restfulemf.ResourceSnapshot;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.api.IResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;

/**
 * Converts canonical resource snapshots to XMI, binary and CSV representations.
 */
@Service
public class ResourceFormatService implements IResourceFormatService {

    private final IResourceSnapshotService resourceSnapshotService;

    private final List<EPackage> registeredPackages;

    private final long maximumUncompressedSize;

    private final Logger logger = LoggerFactory.getLogger(ResourceFormatService.class);

    public ResourceFormatService(IResourceSnapshotService resourceSnapshotService, List<EPackage> registeredPackages, RestfulEMFProperties properties) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.registeredPackages = List.copyOf(Objects.requireNonNull(registeredPackages));
        this.maximumUncompressedSize = Objects.requireNonNull(properties).maxUncompressedSize().toBytes();
    }

    @Override
    public void serializeEPackages(String projectId, OutputStream outputStream) {
        this.serializeEPackages(projectId, ResourceFormat.BINARY, outputStream);
    }

    @Override
    public void serializeEPackages(String projectId, ResourceFormat format, OutputStream outputStream) {
        XMLResource targetResource = new XMIResourceImpl(URI.createURI("sirius:///" + projectId + "/epackages"));
        var copier = new EcoreUtil.Copier();
        this.registeredPackages.forEach(ePackage -> targetResource.getContents().add(copier.copy(ePackage)));
        copier.copyReferences();
        this.save(targetResource, this.getOptions(format), null, outputStream);
    }

    @Override
    public String ePackagesRevision(String projectId, ResourceFormat format) {
        return this.digest(output -> this.serializeEPackages(projectId, format, output));
    }

    @Override
    public String representationRevision(ResourceSnapshot snapshot, ResourceDocument document, List<ResourceDocument> documents, ResourceFormat format, String separator) {
        return this.digest(output -> this.serialize(snapshot, document, documents, format, separator, output));
    }

    private String digest(Consumer<OutputStream> writer) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            writer.accept(new DigestOutputStream(OutputStream.nullOutputStream(), digest));
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required by the Java runtime", exception);
        }
    }

    @Override
    public void serialize(ResourceSnapshot snapshot, ResourceDocument document, ResourceFormat format, String separator, OutputStream outputStream) {
        this.serialize(snapshot, document, List.of(document), format, separator, outputStream);
    }

    @Override
    public void serialize(ResourceSnapshot snapshot, ResourceDocument document, List<ResourceDocument> documents, ResourceFormat format, String separator, OutputStream outputStream) {
        Resource resource = this.loadSnapshot(snapshot, document);
        new ResourceReferences().externalize(resource, document, documents);
        switch (format) {
            case BINARY -> this.serializeBinary(resource, document, outputStream);
            case XMI -> this.serializeXMI(resource, document, false, outputStream);
            case ZIPPED_XMI -> this.serializeXMI(resource, document, true, outputStream);
            case CSV -> this.serializeCSV(resource, separator, document, outputStream);
        }
    }

    @Override
    public ResourceSnapshot deserialize(InputStream content, ResourceDocument document, ResourceFormat format) {
        return this.deserialize(content, document, format, List.of(document), this.createResourceSet());
    }

    @Override
    public void reconcileReferences(ResourceSet resourceSet, List<ResourceDocument> documents) {
        new ResourceReferences().reconcile(resourceSet, documents);
    }

    @Override
    public ResourceSnapshot deserialize(InputStream content, ResourceDocument document, ResourceFormat format, List<ResourceDocument> documents, ResourceSet existingResources) {
        if (format == ResourceFormat.CSV) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "CSV resources cannot be imported");
        }

        Resource resource = new XMIResourceImpl(new ResourceReferences().publicURI(document));
        var resourceSet = this.createResourceSet();
        resourceSet.getPackageRegistry().putAll(existingResources.getPackageRegistry());
        resourceSet.getResources().add(resource);
        try {
            this.load(resource, content, format);
            this.assignIds((XMLResource) resource, document);
            new ResourceReferences().canonicalize(resource, document, documents, existingResources);
            Resource canonicalResource = this.moveToJsonResource(resource, document);
            return this.resourceSnapshotService.getSnapshot(canonicalResource)
                    .orElseThrow(() -> new IllegalArgumentException("The EMF resource could not be serialized"));
        } catch (RestfulEMFException exception) {
            throw exception;
        } catch (IOException | RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("Document content could not be loaded")
                    .addKeyValue("documentId", document.id())
                    .setCause(exception)
                    .log();
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "Invalid EMF document", exception);
        }
    }

    private void load(Resource resource, InputStream content, ResourceFormat format) throws IOException {
        if (format == ResourceFormat.ZIPPED_XMI) {
            try (var zip = new ZipInputStream(StreamUtils.nonClosing(content))) {
                var entry = zip.getNextEntry();
                if (entry == null || entry.isDirectory()) {
                    throw new IOException("The zipped XMI resource must contain one model file");
                }
                resource.load(StreamUtils.nonClosing(new SizeLimitedInputStream(zip, this.maximumUncompressedSize)), this.getOptions(ResourceFormat.XMI));
                if (zip.getNextEntry() != null) {
                    throw new IOException("The zipped XMI resource must contain one model file");
                }
            }
        } else {
            resource.load(content, this.getOptions(format));
        }
    }

    private Resource loadSnapshot(ResourceSnapshot snapshot, ResourceDocument document) {
        var resourceSet = this.createResourceSet();
        var resource = new org.eclipse.sirius.emfjson.resource.JsonResourceImpl(this.createURI(document), Map.of(
                JsonResource.OPTION_ID_MANAGER, new SnapshotEObjectIDManager(document.id()), JsonResource.OPTION_DISPLAY_DYNAMIC_INSTANCES, true));
        resource.setIntrinsicIDToEObjectMap(new HashMap<>());
        resourceSet.getResources().add(resource);
        try (var inputStream = new ByteArrayInputStream(snapshot.content().getBytes(StandardCharsets.UTF_8))) {
            resource.load(inputStream, Map.of());
            return resource;
        } catch (IOException | RuntimeException exception) {
            this.logger.atWarn()
                    .setMessage("EMF resource snapshot could not be loaded")
                    .addKeyValue("documentId", document.id())
                    .setCause(exception)
                    .log();
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF resource snapshot could not be loaded", exception);
        }
    }

    private void serializeBinary(Resource resource, ResourceDocument document, OutputStream outputStream) {
        XMLResource targetResource = new OrderedXMIResource(resource.getURI());
        this.moveContents(resource, targetResource);
        this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), document, outputStream);
    }

    private void serializeXMI(Resource resource, ResourceDocument document, boolean zipped, OutputStream outputStream) {
        XMIResource targetResource = new XMIResourceImpl(resource.getURI());
        this.moveContents(resource, targetResource);
        if (zipped) {
            try (var zip = new ZipOutputStream(StreamUtils.nonClosing(outputStream))) {
                var entry = new ZipEntry("model.xmi");
                entry.setTime(0);
                zip.putNextEntry(entry);
                this.save(targetResource, this.getOptions(ResourceFormat.XMI), document, zip);
                zip.closeEntry();
                zip.finish();
            } catch (IOException exception) {
                this.logSerializationFailure(document, exception);
                throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF resource could not be compressed", exception);
            }
        } else {
            this.save(targetResource, this.getOptions(ResourceFormat.XMI), document, outputStream);
        }
    }

    private void serializeCSV(Resource resource, String separator, ResourceDocument document, OutputStream outputStream) {
        try {
            Set<String> headers = new LinkedHashSet<>(List.of("id"));
            this.forEachCSVRow(resource, row -> headers.addAll(row.keySet()));
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            this.writeCSVLine(writer, separator, new ArrayList<>(headers));
            this.forEachCSVRow(resource, row -> {
                List<String> values = headers.stream().map(header -> row.getOrDefault(header, "")).toList();
                this.writeCSVLine(writer, separator, values);
            });
            writer.flush();
        } catch (IOException | UncheckedIOException exception) {
            this.logSerializationFailure(document, exception);
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF resource could not be serialized", exception);
        }
    }

    private void forEachCSVRow(Resource resource, Consumer<Map<String, String>> consumer) {
        Set<EObject> embeddedObjects = Collections.newSetFromMap(new IdentityHashMap<>());
        var idManager = new EObjectIDManager();
        var iterator = EcoreUtil.<EObject>getAllProperContents(resource, false);
        while (iterator.hasNext()) {
            EObject object = iterator.next();
            if (!object.eIsProxy() && !embeddedObjects.contains(object)) {
                idManager.findId(object).ifPresent(id -> {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("id", id);
                    row.put("eClass", object.eClass().getName());
                    this.putAttributesInRow(row, object);
                    for (EReference reference : object.eClass().getEAllContainments()) {
                        Object value = object.eGet(reference, false);
                        if (!reference.isMany() && value instanceof EObject embeddedObject && !embeddedObject.eIsProxy()) {
                            embeddedObjects.add(embeddedObject);
                            row.put(reference.getName(), embeddedObject.eClass().getName());
                            this.putAttributesInRow(row, embeddedObject);
                        }
                    }
                    consumer.accept(row);
                });
            }
        }
    }

    private void writeCSVLine(Writer writer, String separator, List<String> values) {
        try {
            writer.write(String.join(separator, values));
            writer.write(System.lineSeparator());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private void putAttributesInRow(Map<String, String> row, EObject object) {
        for (EAttribute attribute : object.eClass().getEAllAttributes()) {
            Object value = object.eGet(attribute);
            if (!attribute.isMany() && value != null) {
                String serializedValue = attribute.getEType().getEPackage().getEFactoryInstance().convertToString((EDataType) attribute.getEType(), value);
                if (value instanceof String) {
                    serializedValue = "\"" + serializedValue.replace("\"", "\"\"") + "\"";
                }
                row.put(attribute.getName(), serializedValue);
            }
        }
    }

    private void save(Resource resource, Map<String, Object> options, ResourceDocument document, OutputStream outputStream) {
        try {
            resource.save(outputStream, options);
        } catch (IOException exception) {
            this.logSerializationFailure(document, exception);
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF resource could not be serialized", exception);
        }
    }

    private void logSerializationFailure(ResourceDocument document, Exception exception) {
        var logBuilder = this.logger.atWarn()
                .setMessage("EMF resource could not be serialized")
                .setCause(exception);
        if (document != null) {
            logBuilder.addKeyValue("documentId", document.id());
        }
        logBuilder.log();
    }

    private void moveContents(Resource sourceResource, XMLResource targetResource) {
        var idManager = new EObjectIDManager();
        EcoreUtil.<EObject>getAllProperContents(sourceResource, false).forEachRemaining(object -> {
            if (!object.eIsProxy()) {
                idManager.findId(object).ifPresent(id -> targetResource.setID(object, id));
            }
        });
        targetResource.getContents().addAll(List.copyOf(sourceResource.getContents()));
    }

    private void assignIds(XMLResource resource, ResourceDocument document) {
        Map<EObject, String> ids = new IdentityHashMap<>();
        var references = new ResourceReferences();
        EcoreUtil.<EObject>getAllProperContents(resource, false).forEachRemaining(object -> {
            if (object.eIsProxy()) {
                return;
            }
            String originalId = resource.getID(object);
            String fragment = resource.getURIFragment(object);
            String id = references.objectId(document.id(), fragment);
            if (originalId != null) {
                try {
                    if (UUID.fromString(originalId).toString().equals(originalId)) {
                        id = originalId;
                    }
                } catch (IllegalArgumentException exception) {
                    // Non-UUID source identifiers are mapped deterministically, never written to semantic ID attributes.
                }
            }
            ids.put(object, id);
        });
        if (new HashSet<>(ids.values()).size() != ids.size()) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "Document object identifiers must be unique");
        }
        ids.forEach(resource::setID);
    }

    private Resource moveToJsonResource(Resource sourceResource, ResourceDocument document) {
        JsonResource targetResource = (JsonResource) new JSONResourceFactory().createResource(this.createURI(document));
        var idManager = new EObjectIDManager();
        EcoreUtil.<EObject>getAllProperContents(sourceResource, false).forEachRemaining(object -> {
            if (object.eIsProxy()) {
                return;
            }
            String id = sourceResource instanceof XMLResource xmlResource ? xmlResource.getID(object) : null;
            Optional.ofNullable(id).or(() -> idManager.findId(object)).ifPresent(value -> targetResource.setID(object, value));
        });
        targetResource.getContents().addAll(List.copyOf(sourceResource.getContents()));
        return targetResource;
    }

    private ResourceSetImpl createResourceSet() {
        var resourceSet = new DetachedResourceSet();
        this.registeredPackages.forEach(ePackage -> resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage));
        return resourceSet;
    }

    private Map<String, Object> getOptions(ResourceFormat format) {
        Map<String, Object> options = format == ResourceFormat.BINARY ? new HashMap<>() : new HashMap<>(new EMFResourceUtils().getXMILoadOptions());
        if (format == ResourceFormat.BINARY) {
            options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
        } else if (format == ResourceFormat.ZIPPED_XMI) {
            options.put(Resource.OPTION_ZIP, Boolean.TRUE);
        }
        return options;
    }

    private URI createURI(ResourceDocument document) {
        return new JSONResourceFactory().createResourceURI(document.id().toString());
    }
}
