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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Sinks;

/**
 * Checks collaborative reads without changing the live model.
 */
public class GetResourceContentEventHandlerTests {

    @Test
    public void pathLookupReturnsSnapshotAndNoSemanticChange() {
        var context = this.context();
        var factory = new JSONResourceFactory();
        var resource = factory.createResource(factory.createResourceURI(UUID.randomUUID().toString()));
        resource.eAdapters().add(new ResourceMetadataAdapter("domain/customer.ecore"));
        context.getDomain().getResourceSet().getResources().add(resource);
        var snapshot = new ResourceSnapshot("{}", "revision");
        var handler = new GetResourceContentEventHandler(candidate -> {
            assertThat(candidate).isSameAs(resource);
            return Optional.of(snapshot);
        }, new ResourcePaths());
        var input = new GetResourceContentInput(UUID.randomUUID(), "domain/customer.ecore");
        var payload = Sinks.<IPayload>one();
        var changes = Sinks.many().unicast().<ChangeDescription>onBackpressureBuffer();

        assertThat(handler.canHandle(context, input)).isTrue();
        assertThat(handler.canHandle(mock(IEditingContext.class), input)).isFalse();
        assertThat(handler.canHandle(context, mock(IInput.class))).isFalse();
        handler.handle(payload, changes, context, input);

        assertThat(payload.asMono().block()).isInstanceOfSatisfying(GetResourceContentSuccessPayload.class, result -> {
            assertThat(result.snapshot()).isSameAs(snapshot);
            assertThat(result.id()).isEqualTo(input.id());
            assertThat(result.document().path()).isEqualTo(input.path());
            assertThat(result.documents()).containsExactly(result.document());
        });
        assertThat(changes.asFlux().blockFirst().getKind()).isEqualTo(ChangeKind.NOTHING);
    }

    @Test
    public void missingPathReturnsAnErrorPayload() {
        var handler = new GetResourceContentEventHandler(resource -> Optional.empty(), new ResourcePaths());
        var payload = Sinks.<IPayload>one();
        handler.handle(payload, Sinks.many().unicast().onBackpressureBuffer(), this.context(), new GetResourceContentInput(UUID.randomUUID(), "absent.ecore"));
        assertThat(payload.asMono().block()).isInstanceOf(ErrorPayload.class);
    }

    @Test
    public void invalidPathPropagatesTheFailure() {
        var handler = new GetResourceContentEventHandler(resource -> Optional.empty(), new ResourcePaths());
        var payload = Sinks.<IPayload>one();
        handler.handle(payload, Sinks.many().unicast().onBackpressureBuffer(), this.context(), new GetResourceContentInput(UUID.randomUUID(), "../escape"));
        assertThatThrownBy(() -> payload.asMono().block()).isInstanceOf(RuntimeException.class);
    }

    private IEMFEditingContext context() {
        var context = mock(IEMFEditingContext.class);
        when(context.getId()).thenReturn(UUID.randomUUID().toString());
        when(context.getDomain()).thenReturn(new AdapterFactoryEditingDomain(new AdapterFactoryImpl(), new BasicCommandStack()));
        return context;
    }
}
