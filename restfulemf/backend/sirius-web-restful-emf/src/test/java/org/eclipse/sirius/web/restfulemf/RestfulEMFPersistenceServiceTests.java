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
package org.eclipse.sirius.web.restfulemf;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.events.ICause;
import org.eclipse.sirius.web.application.editingcontext.services.EditingContextPersistenceService;
import org.eclipse.sirius.web.application.editingcontext.services.api.IResourceToDocumentService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.SemanticData;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataUpdateService;
import org.eclipse.sirius.web.restfulemf.application.RestfulEMFPersistenceService;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.junit.jupiter.api.Test;

/**
 * Protects the persistence failure boundary against silently dropping documents.
 */
public class RestfulEMFPersistenceServiceTests {

    @Test
    public void serializationFailureDoesNotUpdateTheAggregate() {
        var search = mock(ISemanticDataSearchService.class);
        var update = mock(ISemanticDataUpdateService.class);
        var serialization = mock(IResourceToDocumentService.class);
        var context = mock(IEMFEditingContext.class);
        var contextId = UUID.randomUUID();
        when(context.getId()).thenReturn(contextId.toString());
        var domain = new AdapterFactoryEditingDomain(new AdapterFactoryImpl(), new BasicCommandStack());
        when(context.getDomain()).thenReturn(domain);
        var factory = new JSONResourceFactory();
        var resource = factory.createResource(factory.createResourceURI(UUID.randomUUID().toString()));
        domain.getResourceSet().getResources().add(resource);
        when(search.findById(contextId)).thenReturn(Optional.of(mock(SemanticData.class)));
        when(serialization.toDocument(resource, false)).thenReturn(Optional.empty());
        var service = new RestfulEMFPersistenceService(search, update, serialization, List.of(), List.of(), mock(EditingContextPersistenceService.class));

        assertThatThrownBy(() -> service.persist(mock(ICause.class), context, resource)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(update);
    }

    @Test
    public void missingAggregateDoesNotUpdateDocuments() {
        var search = mock(ISemanticDataSearchService.class);
        var update = mock(ISemanticDataUpdateService.class);
        var serialization = mock(IResourceToDocumentService.class);
        var context = mock(IEMFEditingContext.class);
        when(context.getId()).thenReturn(UUID.randomUUID().toString());
        var service = new RestfulEMFPersistenceService(search, update, serialization, List.of(), List.of(), mock(EditingContextPersistenceService.class));

        var factory = new JSONResourceFactory();
        var resource = factory.createResource(factory.createResourceURI(UUID.randomUUID().toString()));
        assertThatThrownBy(() -> service.persist(mock(ICause.class), context, resource)).isInstanceOf(IllegalStateException.class);
        verifyNoInteractions(update, serialization);
    }

    @Test
    public void committedRestWriteIsNotPersistedTwiceButOtherChangesAreDelegated() {
        var delegate = mock(EditingContextPersistenceService.class);
        var service = new RestfulEMFPersistenceService(mock(ISemanticDataSearchService.class), mock(ISemanticDataUpdateService.class),
                mock(IResourceToDocumentService.class), List.of(), List.of(), delegate);
        var context = mock(IEMFEditingContext.class);
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), "model.ecore", ResourceFormat.XMI, new byte[0], List.of(), false);

        service.persist(input, context);
        verifyNoInteractions(delegate);
        var cause = mock(ICause.class);
        service.persist(cause, context);
        verify(delegate).persist(cause, context);
    }
}
