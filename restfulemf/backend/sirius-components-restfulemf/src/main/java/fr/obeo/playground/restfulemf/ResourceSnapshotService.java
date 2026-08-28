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
package fr.obeo.playground.restfulemf;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.web.application.editingcontext.services.api.IResourceToDocumentService;
import org.springframework.stereotype.Service;

/**
 * Creates resource snapshots using the canonical Sirius Web persistence format.
 */
@Service
public class ResourceSnapshotService implements IResourceSnapshotService {

    private final IResourceToDocumentService resourceToDocumentService;

    public ResourceSnapshotService(IResourceToDocumentService resourceToDocumentService) {
        this.resourceToDocumentService = Objects.requireNonNull(resourceToDocumentService);
    }

    @Override
    public Optional<ResourceSnapshot> getSnapshot(Resource resource) {
        Resource canonicalResource = resource instanceof JsonResource ? resource : this.toJsonResource(resource);
        return this.resourceToDocumentService.toDocument(canonicalResource, false)
                .map(documentData -> documentData.document().getContent())
                .map(content -> new ResourceSnapshot(content, this.getRevision(content)));
    }

    private Resource toJsonResource(Resource resource) {
        JsonResource targetResource = new JSONResourceFactory().createResource(resource.getURI());
        var copier = new EcoreUtil.Copier();
        targetResource.getContents().addAll(copier.copyAll(resource.getContents()));
        copier.copyReferences();
        var idManager = new EObjectIDManager();
        copier.forEach((sourceObject, copiedObject) -> {
            String id = resource instanceof XMLResource xmlResource ? xmlResource.getID(sourceObject) : null;
            Optional.ofNullable(id).or(() -> idManager.findId(sourceObject)).ifPresent(value -> targetResource.setID(copiedObject, value));
        });
        return targetResource;
    }

    private String getRevision(String content) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
