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
package org.eclipse.sirius.web.restfulemf.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFError;
import org.eclipse.sirius.web.restfulemf.application.api.RestfulEMFException;
import org.eclipse.sirius.web.restfulemf.services.api.ResourceDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Covers public path assignment, collisions and validation independently from persistence.
 *
 * @author cbrun
 */
public class ResourcePathsTests {

    @Test
    public void givenDuplicateAndInvalidNamesWhenAssigningPathsThenFallbacksAreStableAndSorted() {
        var paths = new ResourcePaths();
        var first = new ResourceDocument(UUID.randomUUID(), "duplicate.ecore", false);
        var second = new ResourceDocument(UUID.randomUUID(), first.name(), true);
        var invalid = new ResourceDocument(UUID.randomUUID(), "../outside.ecore", false);
        var valid = new ResourceDocument(UUID.randomUUID(), "domain/été model.ecore", false);
        var assigned = paths.assignPaths(List.of(valid, second, invalid, first));
        assertThat(assigned).extracting(ResourceDocument::path).isSorted();
        assertThat(assigned).filteredOn(document -> !document.id().equals(valid.id()))
                .allSatisfy(document -> assertThat(document.path()).isEqualTo("_by-id/" + document.id()));
        assertThat(paths.find(assigned, valid.name())).contains(valid);
        assertThat(paths.find(assigned, "_by-id/" + second.id())).get().extracting(ResourceDocument::readOnly).isEqualTo(true);
        assertThatThrownBy(() -> paths.find(assigned, first.name())).isInstanceOfSatisfying(RestfulEMFException.class,
                exception -> assertThat(exception.getError()).isEqualTo(RestfulEMFError.CONFLICT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/absolute", "a/", "a//b", ".", "..", "a/../b", "a/./b", "a\\b", "a%2fb", "a?b", "a#b", "a\nb"})
    public void givenInvalidPathWhenValidatingThenItIsRejected(String path) {
        assertThatThrownBy(() -> new ResourcePaths().validate(path)).isInstanceOf(RestfulEMFException.class);
    }

    @Test
    public void givenRenamedDocumentWhenAssigningPathsThenItsIdentityIsPreserved() {
        UUID id = UUID.randomUUID();
        var resources = new DetachedResourceSet();
        var resource = new JSONResourceFactory().createResourceFromPath(id.toString());
        var metadata = new ResourceMetadataAdapter("old/path.ecore", true);
        resource.eAdapters().add(metadata);
        resources.getResources().add(resource);
        var paths = new ResourcePaths();
        var previous = paths.documents(resources).getFirst();

        metadata.setName("new/path.ecore");

        var renamed = paths.documents(resources);
        assertThat(renamed).containsExactly(new ResourceDocument(id, metadata.getName(), true));
        assertThat(paths.find(renamed, previous.path())).isEmpty();
        assertThat(paths.find(renamed, metadata.getName())).get().extracting(ResourceDocument::id).isEqualTo(previous.id());
    }
}
