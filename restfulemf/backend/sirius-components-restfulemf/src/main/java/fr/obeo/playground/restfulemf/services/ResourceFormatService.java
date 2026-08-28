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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.obeo.playground.restfulemf.IResourceSnapshotService;
import fr.obeo.playground.restfulemf.ResourceSnapshot;
import fr.obeo.playground.restfulemf.SheetDataTable;
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

    private final Logger logger = LoggerFactory.getLogger(ResourceFormatService.class);

    public ResourceFormatService(IResourceSnapshotService resourceSnapshotService, List<EPackage> registeredPackages) {
        this.resourceSnapshotService = Objects.requireNonNull(resourceSnapshotService);
        this.registeredPackages = List.copyOf(Objects.requireNonNull(registeredPackages));
    }

    @Override
    public byte[] serializeEPackages(String projectId) {
        XMLResource targetResource = new XMLResourceImpl(URI.createURI("sirius:///" + projectId + "/epackages"));
        var copier = new EcoreUtil.Copier();
        this.registeredPackages.forEach(ePackage -> targetResource.getContents().add(copier.copy(ePackage)));
        copier.copyReferences();
        return this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), null);
    }

    @Override
    public byte[] serialize(ResourceSnapshot snapshot, ResourceDocument document, ResourceFormat format, String separator) {
        Resource resource = this.loadSnapshot(snapshot, document);
        return switch (format) {
            case BINARY -> this.serializeBinary(resource, document);
            case XMI -> this.serializeXMI(resource, document, false);
            case ZIPPED_XMI -> this.serializeXMI(resource, document, true);
            case CSV -> this.serializeCSV(resource, separator).getBytes(StandardCharsets.UTF_8);
        };
    }

    @Override
    public ResourceSnapshot deserialize(byte[] content, ResourceDocument document, ResourceFormat format) {
        if (format == ResourceFormat.CSV) {
            throw new RestfulEMFException(RestfulEMFError.INVALID_RESOURCE, "CSV resources cannot be imported");
        }

        Resource resource = format == ResourceFormat.BINARY ? new XMLResourceImpl(this.createURI(document)) : new XMIResourceImpl(this.createURI(document));
        var resourceSet = this.createResourceSet();
        resourceSet.getResources().add(resource);
        try (var inputStream = new ByteArrayInputStream(content)) {
            resource.load(inputStream, this.getOptions(format));
            return this.resourceSnapshotService.getSnapshot(resource)
                    .orElseThrow(() -> new IllegalArgumentException("The EMF resource could not be serialized"));
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

    private byte[] serializeBinary(Resource resource, ResourceDocument document) {
        XMLResource targetResource = new XMLResourceImpl(resource.getURI());
        this.copyContents(resource, targetResource);
        return this.save(targetResource, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE), document);
    }

    private byte[] serializeXMI(Resource resource, ResourceDocument document, boolean zipped) {
        XMIResource targetResource = new XMIResourceImpl(resource.getURI());
        this.copyContents(resource, targetResource);
        Map<String, Object> options = this.getOptions(zipped ? ResourceFormat.ZIPPED_XMI : ResourceFormat.XMI);
        return this.save(targetResource, options, document);
    }

    private String serializeCSV(Resource resource, String separator) {
        var table = new SheetDataTable();
        var idManager = new EObjectIDManager();
        Set<EObject> ignoredObjects = Collections.newSetFromMap(new IdentityHashMap<>());

        resource.getAllContents().forEachRemaining(object -> {
            if (!ignoredObjects.contains(object)) {
                idManager.findId(object).ifPresent(id -> {
                    table.updateValue(id, "eClass", object.eClass().getName());
                    this.putAttributesInTable(table, object, id);
                    for (EReference containment : object.eClass().getEAllContainments()) {
                        if (containment.getUpperBound() == 1 && object.eGet(containment) instanceof EObject child) {
                            ignoredObjects.add(child);
                            table.updateValue(id, containment.getName(), child.eClass().getName());
                            this.putAttributesInTable(table, child, id);
                        }
                    }
                });
            }
        });

        table.fillEmptyCells();
        return table.getValues().stream()
                .map(line -> String.join(separator, line))
                .collect(Collectors.joining("\n", "", "\n"));
    }

    private void putAttributesInTable(SheetDataTable table, EObject object, String objectId) {
        for (EAttribute attribute : object.eClass().getEAllAttributes()) {
            Object value = object.eGet(attribute);
            if (!attribute.isMany() && value != null) {
                String serializedValue = attribute.getEType().getEPackage().getEFactoryInstance().convertToString((EDataType) attribute.getEType(), value);
                if (value instanceof String) {
                    serializedValue = "\"" + serializedValue.replace("\"", "\"\"") + "\"";
                }
                table.updateValue(objectId, attribute.getName(), serializedValue);
            }
        }
    }

    private byte[] save(Resource resource, Map<String, Object> options, ResourceDocument document) {
        try (var outputStream = new ByteArrayOutputStream()) {
            resource.save(outputStream, options);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            var loggingEvent = this.logger.atWarn()
                    .setMessage("EMF resource serialization failed")
                    .setCause(exception);
            if (document != null) {
                loggingEvent.addKeyValue("documentId", document.id());
            }
            loggingEvent.log();
            throw new RestfulEMFException(RestfulEMFError.PROCESSING_FAILURE, "The EMF resource could not be serialized", exception);
        }
    }

    private void copyContents(Resource sourceResource, XMLResource targetResource) {
        var copier = new EcoreUtil.Copier();
        targetResource.getContents().addAll(copier.copyAll(sourceResource.getContents()));
        copier.copyReferences();
        var idManager = new EObjectIDManager();
        copier.forEach((sourceObject, copiedObject) -> idManager.findId(sourceObject).ifPresent(id -> targetResource.setID(copiedObject, id)));
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
