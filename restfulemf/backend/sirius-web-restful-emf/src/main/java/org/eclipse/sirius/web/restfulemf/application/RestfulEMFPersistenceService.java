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
package org.eclipse.sirius.web.restfulemf.application;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextPersistenceService;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.events.ICause;
import org.eclipse.sirius.web.application.editingcontext.services.EditingContextPersistenceService;
import org.eclipse.sirius.web.application.editingcontext.services.api.IEditingContextMigrationParticipantPredicate;
import org.eclipse.sirius.web.application.editingcontext.services.api.IEditingContextPersistenceFilter;
import org.eclipse.sirius.web.application.editingcontext.services.api.IResourceToDocumentService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.Document;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataUpdateService;
import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentInput;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Commits a REST write before its successful HTTP response can be emitted.
 */
@Service
@Primary
public class RestfulEMFPersistenceService implements IEditingContextPersistenceService {

    private final EditingContextPersistenceService delegate;

    private final ISemanticDataSearchService semanticDataSearchService;

    private final ISemanticDataUpdateService semanticDataUpdateService;

    private final IResourceToDocumentService resourceToDocumentService;

    private final List<IEditingContextPersistenceFilter> persistenceFilters;

    private final List<IEditingContextMigrationParticipantPredicate> migrationPredicates;

    public RestfulEMFPersistenceService(ISemanticDataSearchService semanticDataSearchService, ISemanticDataUpdateService semanticDataUpdateService,
            IResourceToDocumentService resourceToDocumentService, List<IEditingContextPersistenceFilter> persistenceFilters,
            List<IEditingContextMigrationParticipantPredicate> migrationPredicates, EditingContextPersistenceService delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.semanticDataSearchService = Objects.requireNonNull(semanticDataSearchService);
        this.semanticDataUpdateService = Objects.requireNonNull(semanticDataUpdateService);
        this.resourceToDocumentService = Objects.requireNonNull(resourceToDocumentService);
        this.persistenceFilters = List.copyOf(persistenceFilters);
        this.migrationPredicates = List.copyOf(migrationPredicates);
    }

    @Override
    public void persist(ICause cause, IEditingContext editingContext) {
        if (!(cause instanceof ReplaceResourceContentInput)) {
            this.delegate.persist(cause, editingContext);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persist(ICause cause, IEMFEditingContext editingContext, Resource target) {
        if (this.persistenceFilters.stream().anyMatch(filter -> !filter.shouldPersist(target))) {
            throw new IllegalStateException("The target document is excluded from persistence");
        }
        var semanticDataId = UUID.fromString(editingContext.getId());
        var semanticData = this.semanticDataSearchService.findById(semanticDataId)
                .orElseThrow(() -> new IllegalStateException("The semantic data no longer exists"));
        var documents = new LinkedHashSet<Document>();
        var domainUris = new LinkedHashSet<String>();
        boolean applyMigrations = this.migrationPredicates.stream().anyMatch(predicate -> predicate.test(editingContext.getId()));
        for (var resource : List.copyOf(editingContext.getDomain().getResourceSet().getResources())) {
            if (IEMFEditingContext.RESOURCE_SCHEME.equals(resource.getURI().scheme())
                    && this.persistenceFilters.stream().allMatch(filter -> filter.shouldPersist(resource))) {
                var data = this.resourceToDocumentService.toDocument(resource, applyMigrations)
                        .orElseThrow(() -> new IllegalStateException("An EMF resource could not be serialized"));
                var document = data.document();
                if (document.isReadOnly()) {
                    document = semanticData.getDocuments().stream()
                            .filter(existing -> existing.isReadOnly() && existing.getId().equals(data.document().getId()))
                            .findFirst().orElse(document);
                }
                documents.add(document);
                data.ePackageEntries().forEach(entry -> domainUris.add(entry.nsURI()));
            }
        }
        this.semanticDataUpdateService.updateDocuments(cause, AggregateReference.to(semanticDataId), documents, domainUris);
        var persisted = this.semanticDataSearchService.findById(semanticDataId)
                .orElseThrow(() -> new IllegalStateException("The semantic data disappeared during persistence"));
        var persistedById = persisted.getDocuments().stream().collect(Collectors.toMap(Document::getId, Function.identity()));
        if (persistedById.size() != documents.size() || documents.stream().anyMatch(document -> {
            var stored = persistedById.get(document.getId());
            return stored == null || !stored.getName().equals(document.getName()) || !stored.getContent().equals(document.getContent())
                    || stored.isReadOnly() != document.isReadOnly();
        })) {
            throw new IllegalStateException("The semantic data update was not persisted");
        }
    }
}
