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
package fr.obeo.dsl.guesstimate.configuration;

import fr.obeo.dsl.guesstimate.GuesstimateFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.migration.MigrationService;
import org.eclipse.sirius.components.emf.migration.api.IMigrationParticipant;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.web.application.document.dto.DocumentDTO;
import org.eclipse.sirius.web.application.document.services.api.IStereotypeHandler;
import org.eclipse.sirius.web.application.views.explorer.services.ExplorerDescriptionProvider;
import org.springframework.stereotype.Service;

/**
 * Creates Guesstimate documents.
 *
 * @author cedric
 */
@Service
public class GuesstimateStereotypeHandler implements IStereotypeHandler {

    private final List<IMigrationParticipant> migrationParticipants;

    public GuesstimateStereotypeHandler(List<IMigrationParticipant> migrationParticipants) {
        this.migrationParticipants = Objects.requireNonNull(migrationParticipants);
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, String stereotypeId) {
        return editingContext instanceof IEMFEditingContext && GuesstimateStereotypeProvider.GUESSTIMATE_STEREOTYPE_ID.equals(stereotypeId);
    }

    @Override
    public Optional<DocumentDTO> handle(IEditingContext editingContext, String stereotypeId, String name) {
        if (this.canHandle(editingContext, stereotypeId) && editingContext instanceof IEMFEditingContext emfEditingContext) {
            var documentId = UUID.randomUUID();
            var resource = new JSONResourceFactory().createResourceFromPath(documentId.toString());
            var resourceMetadataAdapter = new ResourceMetadataAdapter(name);
            resourceMetadataAdapter.addMigrationData(new MigrationService(this.migrationParticipants).getMostRecentParticipantMigrationData());
            resource.eAdapters().add(resourceMetadataAdapter);
            resource.getContents().add(GuesstimateFactory.eINSTANCE.createSheet());
            emfEditingContext.getDomain().getResourceSet().getResources().add(resource);
            return Optional.of(new DocumentDTO(documentId, name, ExplorerDescriptionProvider.DOCUMENT_KIND));
        }
        return Optional.empty();
    }
}
