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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.graphql.api.IEditingContextDispatcher;
import org.eclipse.sirius.web.restfulemf.GetResourceContentSuccessPayload;
import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentInput;
import org.eclipse.sirius.web.restfulemf.ReplaceResourceContentSuccessPayload;
import org.eclipse.sirius.web.restfulemf.ResourceRevisionConflictPayload;
import org.eclipse.sirius.web.restfulemf.ResourceSnapshot;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceFormat;
import org.eclipse.sirius.web.restfulemf.application.api.ResourceWriteStatus;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.ResourcePaths;
import org.eclipse.sirius.web.restfulemf.services.api.IProjectDocumentsService;
import org.eclipse.sirius.web.restfulemf.services.api.IResourceFormatService;
import org.eclipse.sirius.web.restfulemf.services.api.ProjectDocuments;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

import reactor.core.publisher.Mono;

/**
 * Verifies application orchestration and errors at the collaborative dispatcher boundary.
 */
public class RestfulEMFApplicationServicesTests {

    private static final String PROJECT = "project";

    private static final String CONTEXT = "context";

    private static final String PATH = "domain/model.ecore";

    private static final String REVISION = "revision";

    @Test
    public void givenSnapshotWhenReadingThenTheSnapshotMetadataAndFormatDriveTheRepresentation() throws IOException {
        var document = new ResourceDocument(UUID.randomUUID(), PATH, false);
        var documents = List.of(document);
        var projectService = this.projects(documents);
        var dispatcher = mock(IEditingContextDispatcher.class);
        var formats = mock(IResourceFormatService.class);
        var snapshot = new ResourceSnapshot("{}", REVISION);
        when(dispatcher.dispatchQuery(eq(CONTEXT), any())).thenReturn(Mono.just(new GetResourceContentSuccessPayload(UUID.randomUUID(), snapshot, document, documents)));
        when(formats.representationRevision(snapshot, document, documents, ResourceFormat.XMI, "\t")).thenReturn(REVISION);
        var service = new RestfulEMFReadApplicationService(projectService, dispatcher, formats, new ResourcePaths());
        assertThat(service.getDocuments(PROJECT)).containsExactly(document);
        var representation = service.getResource(PROJECT, PATH, ResourceFormat.XMI, "\t");
        var output = new ByteArrayOutputStream();
        representation.writer().write(output);
        assertThat(representation.revision()).isEqualTo(REVISION);
        verify(formats).serialize(snapshot, document, documents, ResourceFormat.XMI, "\t", output);
        when(formats.ePackagesRevision(PROJECT, ResourceFormat.BINARY)).thenReturn(REVISION);
        var packages = service.getEPackages(PROJECT, ResourceFormat.BINARY);
        packages.writer().write(output);
        assertThat(packages.revision()).isEqualTo(REVISION);
        verify(formats).serializeEPackages(PROJECT, ResourceFormat.BINARY, output);
    }

    @Test
    public void givenMissingProjectWhenReadingOrWritingThenNotFoundIsReported() {
        var projects = mock(IProjectDocumentsService.class);
        var dispatcher = mock(IEditingContextDispatcher.class);
        var read = new RestfulEMFReadApplicationService(projects, dispatcher, mock(IResourceFormatService.class), new ResourcePaths());
        var write = new RestfulEMFWriteApplicationService(projects, dispatcher);
        assertThatThrownBy(() -> read.getDocuments(PROJECT)).isInstanceOfSatisfying(RestfulEMFException.class,
                exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.NOT_FOUND));
        assertThatThrownBy(() -> write.replaceResource(PROJECT, PATH, ResourceFormat.XMI, InputStream.nullInputStream(), List.of(), false))
                .isInstanceOfSatisfying(RestfulEMFException.class, exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.NOT_FOUND));
    }

    @ParameterizedTest
    @EnumSource(value = RestfulEMFError.class, names = {"NOT_FOUND", "TIMEOUT", "PROCESSING_FAILURE", "CONFLICT"})
    public void givenReadFailureWhenDispatchingThenTheApplicationErrorIsPreserved(RestfulEMFError expected) {
        var dispatcher = mock(IEditingContextDispatcher.class);
        Mono<IPayload> result = switch (expected) {
            case NOT_FOUND -> Mono.just(new ErrorPayload(UUID.randomUUID(), "missing"));
            case TIMEOUT -> Mono.error(new TimeoutException());
            case PROCESSING_FAILURE -> Mono.error(new IllegalStateException("broken"));
            default -> Mono.error(new RestfulEMFException(expected, "failure"));
        };
        when(dispatcher.dispatchQuery(eq(CONTEXT), any())).thenReturn(result);
        var service = new RestfulEMFReadApplicationService(this.projects(List.of()), dispatcher, mock(IResourceFormatService.class), new ResourcePaths());
        assertThatThrownBy(() -> service.getResource(PROJECT, PATH, ResourceFormat.XMI, "\t")).isInstanceOfSatisfying(RestfulEMFException.class,
                exception -> assertThat(exception.getError()).isEqualTo(expected));
    }

    @ParameterizedTest
    @EnumSource(ResourceWriteStatus.class)
    public void givenWriteResultWhenDispatchingThenStatusAndRequestArePreserved(ResourceWriteStatus status) {
        var dispatcher = mock(IEditingContextDispatcher.class);
        IPayload payload = status == ResourceWriteStatus.CONFLICT ? new ResourceRevisionConflictPayload(UUID.randomUUID(), REVISION)
                : new ReplaceResourceContentSuccessPayload(UUID.randomUUID(), status);
        when(dispatcher.dispatchMutation(eq(CONTEXT), any())).thenReturn(Mono.just(payload));
        var service = new RestfulEMFWriteApplicationService(this.projects(List.of()), dispatcher);
        byte[] bytes = {1, 2, 3};
        var result = service.replaceResource(PROJECT, PATH, ResourceFormat.BINARY, new ByteArrayInputStream(bytes), List.of(REVISION), false);
        assertThat(result.status()).isEqualTo(status);
        assertThat(result.revision()).isEqualTo(status == ResourceWriteStatus.CONFLICT ? REVISION : "");
        var input = ArgumentCaptor.forClass(ReplaceResourceContentInput.class);
        verify(dispatcher).dispatchMutation(eq(CONTEXT), input.capture());
        assertThat(input.getValue().content()).containsExactly(bytes);
        assertThat(input.getValue().path()).isEqualTo(PATH);
        assertThat(input.getValue().expectedRevisions()).containsExactly(REVISION);
        assertThat(input.getValue().createOnly()).isFalse();
    }

    @Test
    public void givenUnreadableBodyWhenWritingThenInvalidResourceIsReported() throws IOException {
        var input = mock(InputStream.class);
        when(input.readAllBytes()).thenThrow(new IOException("disconnected"));
        var service = new RestfulEMFWriteApplicationService(this.projects(List.of()), mock(IEditingContextDispatcher.class));
        assertThatThrownBy(() -> service.replaceResource(PROJECT, PATH, ResourceFormat.XMI, input, List.of(), false))
                .isInstanceOfSatisfying(RestfulEMFException.class, exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.INVALID_RESOURCE));
    }

    @Test
    public void givenInvalidWritePayloadsWhenDispatchingThenTheyCannotBecomeSuccess() {
        var dispatcher = mock(IEditingContextDispatcher.class);
        var service = new RestfulEMFWriteApplicationService(this.projects(List.of()), dispatcher);
        List<Mono<IPayload>> failures = List.of(Mono.empty(), Mono.just(new ErrorPayload(UUID.randomUUID(), "failed")),
                Mono.just(mock(IPayload.class)), Mono.error(new IllegalStateException("broken")));
        for (var failure : failures) {
            when(dispatcher.dispatchMutation(eq(CONTEXT), any())).thenReturn(failure);
            assertThatThrownBy(() -> service.replaceResource(PROJECT, PATH, ResourceFormat.XMI, InputStream.nullInputStream(), List.of(), true))
                    .isInstanceOfSatisfying(RestfulEMFException.class, exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.PROCESSING_FAILURE));
        }
        when(dispatcher.dispatchMutation(eq(CONTEXT), any())).thenReturn(Mono.error(new TimeoutException()));
        assertThatThrownBy(() -> service.replaceResource(PROJECT, PATH, ResourceFormat.XMI, InputStream.nullInputStream(), List.of(), false))
                .isInstanceOfSatisfying(RestfulEMFException.class, exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.TIMEOUT));
    }

    private IProjectDocumentsService projects(List<ResourceDocument> documents) {
        var service = mock(IProjectDocumentsService.class);
        when(service.findByProjectId(PROJECT)).thenReturn(Optional.of(new ProjectDocuments(CONTEXT, documents)));
        return service;
    }
}

