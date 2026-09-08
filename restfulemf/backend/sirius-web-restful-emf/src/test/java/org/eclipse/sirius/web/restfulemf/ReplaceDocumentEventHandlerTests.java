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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.web.restfulemf.application.RestfulEMFPersistenceService;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.configuration.RestfulEMFProperties;
import org.eclipse.sirius.web.restfulemf.services.ResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import reactor.core.publisher.Sinks;

/**
 * Exercises real EMF rollback while injecting only the durable persistence failure.
 *
 * @author cbrun
 */
public class ReplaceDocumentEventHandlerTests {

    private static final String TARGET_PATH = "target.ecore";

    @Test
    public void importingAReferenceToAnExistingDocumentDoesNotTreatItsRelativeUriAsAMissingPackage() {
        var domain = new AdapterFactoryEditingDomain(new AdapterFactoryImpl(), new BasicCommandStack());
        domain.getResourceSet().getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var context = mock(IEMFEditingContext.class);
        when(context.getDomain()).thenReturn(domain);
        when(context.getId()).thenReturn(UUID.randomUUID().toString());
        var factory = new JSONResourceFactory();
        var target = factory.createResource(factory.createResourceURI(UUID.randomUUID().toString()));
        target.eAdapters().add(new ResourceMetadataAdapter(TARGET_PATH));
        var parent = EcoreFactory.eINSTANCE.createEClass();
        parent.setName("Parent");
        target.getContents().add(parent);
        String objectId = UUID.randomUUID().toString();
        target.setID(parent, objectId);
        domain.getResourceSet().getResources().add(target);
        var format = new ResourceFormatService(this::snapshot, List.of(EcorePackage.eINSTANCE),
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 2));
        var handler = new ReplaceDocumentEventHandler(this::snapshot, format, new ResourcePaths(),
                mock(RestfulEMFPersistenceService.class), new SimpleMeterRegistry());
        String body = "<ecore:EClass xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" name=\"Child\">"
                + "<eSuperTypes href=\"" + TARGET_PATH + "#" + objectId + "\"/></ecore:EClass>";
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), "child.ecore", ResourceFormat.XMI,
                body.getBytes(StandardCharsets.UTF_8), List.of(), true);
        var payload = Sinks.<IPayload>one();

        handler.handle(payload, Sinks.many().unicast().onBackpressureBuffer(), context, input);

        assertThat(payload.asMono().block()).isInstanceOf(ReplaceResourceContentSuccessPayload.class);
        var childResource = domain.getResourceSet().getResources().getLast();
        assertThat(childResource.getErrors()).isEmpty();
        assertThat(childResource.getContents()).singleElement().isInstanceOfSatisfying(EClass.class,
                child -> assertThat(child.getESuperTypes()).containsExactly(parent));
    }

    @Test
    public void failedCommitRestoresTargetAndPendingReferencesWithoutReplacingOtherObjects() {
        var domain = new AdapterFactoryEditingDomain(new AdapterFactoryImpl(), new BasicCommandStack());
        domain.getResourceSet().getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var context = mock(IEMFEditingContext.class);
        when(context.getDomain()).thenReturn(domain);
        when(context.getId()).thenReturn(UUID.randomUUID().toString());
        var factory = new JSONResourceFactory();
        var target = factory.createResource(factory.createResourceURI(UUID.randomUUID().toString()));
        target.eAdapters().add(new ResourceMetadataAdapter(TARGET_PATH));
        var original = EcoreFactory.eINSTANCE.createEClass();
        original.setName("original");
        target.getContents().add(original);
        String objectId = UUID.randomUUID().toString();
        target.setID(original, objectId);
        var source = factory.createResource(factory.createResourceURI(UUID.randomUUID().toString()));
        source.eAdapters().add(new ResourceMetadataAdapter("source.ecore"));
        var referencing = EcoreFactory.eINSTANCE.createEClass();
        source.getContents().add(referencing);
        referencing.getESuperTypes().add(original);
        var pending = EcoreFactory.eINSTANCE.createEClass();
        URI pendingURI = URI.createURI("restfulemf:/documents/" + TARGET_PATH).appendFragment(objectId);
        ((InternalEObject) pending).eSetProxyURI(pendingURI);
        referencing.getESuperTypes().add(pending);
        domain.getResourceSet().getResources().addAll(List.of(target, source));
        var persistence = mock(RestfulEMFPersistenceService.class);
        doAnswer(invocation -> {
            referencing.getESuperTypes().get(1);
            throw new IllegalStateException("Injected persistence failure");
        }).when(persistence).persist(any(), any(), any());
        var format = new ResourceFormatService(this::snapshot, List.of(EcorePackage.eINSTANCE),
                new RestfulEMFProperties(false, DataSize.ofMegabytes(1), DataSize.ofMegabytes(1), 2));
        var handler = new ReplaceDocumentEventHandler(this::snapshot, format, new ResourcePaths(), persistence, new SimpleMeterRegistry());
        String body = "<ecore:EClass xmlns:ecore=\"http://www.eclipse.org/emf/2002/Ecore\" xmlns:xmi=\"http://www.omg.org/XMI\" xmi:id=\""
                + objectId + "\" name=\"replacement\"/>";
        var input = new ReplaceResourceContentInput(UUID.randomUUID(), TARGET_PATH, ResourceFormat.XMI, body.getBytes(StandardCharsets.UTF_8), List.of(), false);
        var payload = Sinks.<IPayload>one();

        handler.handle(payload, Sinks.many().unicast().<ChangeDescription>onBackpressureBuffer(), context, input);

        assertThatThrownBy(() -> payload.asMono().block()).isInstanceOf(RestfulEMFException.class);
        assertThat(source.getContents().getFirst()).isSameAs(referencing);
        assertThat(target.getEObject(objectId).eGet(EcorePackage.Literals.ENAMED_ELEMENT__NAME)).isEqualTo("original");
        assertThat(referencing.getESuperTypes().getFirst()).isSameAs(target.getEObject(objectId));
        assertThat(((org.eclipse.emf.ecore.util.InternalEList<?>) referencing.getESuperTypes()).basicGet(1)).isSameAs(pending);
        assertThat(((InternalEObject) pending).eProxyURI()).isEqualTo(pendingURI);
    }

    private Optional<ResourceSnapshot> snapshot(Resource resource) {
        try {
            var output = new ByteArrayOutputStream();
            resource.save(output, Map.of(JsonResource.OPTION_ID_MANAGER, new EObjectIDManager(), JsonResource.OPTION_SCHEMA_LOCATION, true));
            return Optional.of(new ResourceSnapshot(output.toString(StandardCharsets.UTF_8), "revision"));
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
