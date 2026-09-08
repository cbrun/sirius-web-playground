/*******************************************************************************
 * Copyright (c) 2023, 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.sirius.web.restfulemf.sample.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextPersistenceService;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.events.ICause;
import org.eclipse.sirius.components.graphql.api.UploadFile;
import org.eclipse.sirius.web.application.document.services.api.IUploadFileLoader;
import org.eclipse.sirius.web.application.project.services.api.ISemanticDataInitializer;
import org.eclipse.sirius.web.domain.services.Failure;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * Initializes the playground project templates from their bundled models.
 *
 * @author cbrun
 */
@Service
public class ManyModelsProjectTemplatesInitializer implements ISemanticDataInitializer {

    private static final List<String> MANY_MODELS = List.of("NobelPrize.bpmn", "Big_Guy.flow", "linux-kernel.uml", "library.ecore", "reverse1.ecorebin");

    private static final List<String> ONE_MILLION_MODELS = IntStream.rangeClosed(1, 20)
            .mapToObj(index -> "1Modeling/reverse" + index + ".ecorebin")
            .toList();

    private final IUploadFileLoader uploadFileLoader;

    private final IEditingContextPersistenceService editingContextPersistenceService;

    public ManyModelsProjectTemplatesInitializer(IUploadFileLoader uploadFileLoader, IEditingContextPersistenceService editingContextPersistenceService) {
        this.uploadFileLoader = Objects.requireNonNull(uploadFileLoader);
        this.editingContextPersistenceService = Objects.requireNonNull(editingContextPersistenceService);
    }

    @Override
    public boolean canHandle(String projectTemplateId) {
        return ManyModelsProjectTemplatesProvider.MANY_MODELS_TEMPLATE_ID.equals(projectTemplateId)
                || ManyModelsProjectTemplatesProvider.ONE_MILLION_TEMPLATE_ID.equals(projectTemplateId);
    }

    @Override
    public void handle(ICause cause, IEditingContext editingContext, String projectTemplateId) {
        if (editingContext instanceof IEMFEditingContext emfEditingContext) {
            var modelPaths = ONE_MILLION_MODELS;
            if (ManyModelsProjectTemplatesProvider.MANY_MODELS_TEMPLATE_ID.equals(projectTemplateId)) {
                modelPaths = MANY_MODELS;
            }
            modelPaths.forEach(modelPath -> this.load(emfEditingContext, modelPath));
            this.editingContextPersistenceService.persist(cause, editingContext);
        }
    }

    private void load(IEMFEditingContext editingContext, String modelPath) {
        String sourcePath = modelPath;
        if (modelPath.startsWith("1Modeling/")) {
            sourcePath = "1Modeling/reverse1.ecorebin";
        }
        var classPathResource = new ClassPathResource(sourcePath);
        try {
            if (modelPath.endsWith(".ecorebin")) {
                this.loadBinary(editingContext, classPathResource, URI.createURI(modelPath).lastSegment());
                return;
            }
            try (var inputStream = classPathResource.getInputStream()) {
                var uploadFile = new UploadFile(classPathResource.getFilename(), inputStream);
                var result = this.uploadFileLoader.load(editingContext.getDomain().getResourceSet(), editingContext, uploadFile, false, false);
                if (result instanceof Failure<?> failure) {
                    throw new IllegalStateException("Could not initialize " + modelPath + ": " + failure.message());
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + modelPath, exception);
        }
    }

    private void loadBinary(IEMFEditingContext editingContext, ClassPathResource classPathResource, String documentName) throws IOException {
        var source = new XMLResourceImpl(URI.createURI(documentName));
        try (var inputStream = classPathResource.getInputStream()) {
            source.load(inputStream, Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE));
        }

        var target = new JSONResourceFactory().createResourceFromPath(UUID.randomUUID().toString());
        target.eAdapters().add(new ResourceMetadataAdapter(documentName));
        target.getContents().addAll(source.getContents());
        editingContext.getDomain().getResourceSet().getResources().add(target);
    }
}
