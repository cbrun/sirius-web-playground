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
package fr.obeo.playground.restfulemf.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipInputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import fr.obeo.playground.restfulemf.IResourceSnapshotService;
import fr.obeo.playground.restfulemf.ResourceSnapshot;
import fr.obeo.playground.restfulemf.application.api.ResourceFormat;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFError;
import fr.obeo.playground.restfulemf.application.api.RestfulEMFException;
import fr.obeo.playground.restfulemf.services.api.IResourceFormatService;
import fr.obeo.playground.restfulemf.services.api.ResourceDocument;

/**
 * Converts canonical resource snapshots to XMI, binary and CSV representations.
 */
@Service
public class ResourceFormatService implements IResourceFormatService {

    private final IResourceSnapshotService resourceSnapshotService;

    private final List<EPackage> registeredPackages;

    private final long maximumUncompressedSize;

    private final Logger logger = LoggerFactory.getLogger(ResourceFormatService.class);

    public ResourceFormatService(IResourceSnapshotService resourceSnapshotService, List<EPackage> registeredPackages,
            @Value("${sirius.web.restfulemf.max-uncompressed-size:256MB}") DataSize maximumUncompressedSize) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.registeredPackages = List.copyOf(Objects.requireNonNull(registeredPackages));
        this.maximumUncompressedSize = Objects.requireNonNull(maximumUncompressedSize).toBytes();
        if (this.maximumUncompressedSize < 1) {
            throw new IllegalArgumentException("The maximum uncompressed size must be positive");
        }
    }

    @Override
    public void serializeEPackages(String projectId, OutputStream outputStream) {
        XMLResource targetResource = new XMLResourceImpl(URI.createURI("sirius:///" + projectId + "/epackages"));
        var copier = new EcoreUtil.Copier();
        this.registeredPackages.forEach(ePackage -> targetResource.getContents().add(copier.copy(ePackage)));
        copier.copyReferences();
        this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), null, outputStream);
    }

    @Override
    public void serialize(ResourceSnapshot snapshot, ResourceDocument document, ResourceFormat format, String separator, OutputStream outputStream) {
        Resource resource = this.loadSnapshot(snapshot, document);
        switch (format) {
            case BINARY -> this.serializeBinary(resource, document, outputStream);
            case XMI -> this.serializeXMI(resource, document, false, outputStream);
            case ZIPPED_XMI -> this.serializeXMI(resource, document, true, outputStream);
            case CSV -> this.serializeCSV(resource, separator, document, outputStream);
        }
    }

    @Override
    public ResourceSnapshot deserialize(InputStream content, ResourceDocument document, ResourceFormat format) {
        if (format == ResourceFormat.CSV) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "CSV resources cannot be imported");
        }

        Resource resource = format == ResourceFormat.BINARY ? new XMLResourceImpl(this.createURI(document)) : new XMIResourceImpl(this.createURI(document));
        var resourceSet = this.createResourceSet();
        resourceSet.getResources().add(resource);
        try {
            InputStream resourceContent = content;
            Map<String, Object> options = this.getOptions(format);
            if (format == ResourceFormat.ZIPPED_XMI) {
                var zipInputStream = new ZipInputStream(content);
                if (zipInputStream.getNextEntry() == null) {
                    throw new IOException("The zipped XMI resource is empty");
                }
                resourceContent = new SizeLimitedInputStream(zipInputStream, this.maximumUncompressedSize);
                options = this.getOptions(ResourceFormat.XMI);
            }
            resource.load(resourceContent, options);
            Resource canonicalResource = this.moveToJsonResource(resource);
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

    private Resource loadSnapshot(ResourceSnapshot snapshot, ResourceDocument document) {
        var resourceSet = this.createResourceSet();
        Resource resource = new JSONResourceFactory().createResource(this.createURI(document));
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
        XMLResource targetResource = new XMLResourceImpl(resource.getURI());
        this.moveContents(resource, targetResource);
        this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), document, outputStream);
    }

    private void serializeXMI(Resource resource, ResourceDocument document, boolean zipped, OutputStream outputStream) {
        XMIResource targetResource = new XMIResourceImpl(resource.getURI());
        this.moveContents(resource, targetResource);
        Map<String, Object> options = this.getOptions(zipped ? ResourceFormat.ZIPPED_XMI : ResourceFormat.XMI);
        this.save(targetResource, options, document, outputStream);
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
        var iterator = resource.getAllContents();
        while (iterator.hasNext()) {
            EObject object = iterator.next();
            if (!embeddedObjects.contains(object)) {
                idManager.findId(object).ifPresent(id -> {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("id", id);
                    row.put("eClass", object.eClass().getName());
                    this.putAttributesInRow(row, object);
                    for (EReference reference : object.eClass().getEAllContainments()) {
                        Object value = object.eGet(reference);
                        if (!reference.isMany() && value instanceof EObject embeddedObject) {
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
        sourceResource.getAllContents().forEachRemaining(object -> idManager.findId(object).ifPresent(id -> targetResource.setID(object, id)));
        targetResource.getContents().addAll(List.copyOf(sourceResource.getContents()));
    }

    private Resource moveToJsonResource(Resource sourceResource) {
        JsonResource targetResource = (JsonResource) new JSONResourceFactory().createResource(sourceResource.getURI());
        var idManager = new EObjectIDManager();
        sourceResource.getAllContents().forEachRemaining(object -> {
            String id = sourceResource instanceof XMLResource xmlResource ? xmlResource.getID(object) : null;
            Optional.ofNullable(id).or(() -> idManager.findId(object)).ifPresent(value -> targetResource.setID(object, value));
        });
        targetResource.getContents().addAll(List.copyOf(sourceResource.getContents()));
        return targetResource;
    }

    private ResourceSetImpl createResourceSet() {
        var resourceSet = new ResourceSetImpl();
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
