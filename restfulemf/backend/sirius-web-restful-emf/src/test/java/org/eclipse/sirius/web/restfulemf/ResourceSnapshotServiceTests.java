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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.web.application.editingcontext.services.DocumentData;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.Document;
import org.junit.jupiter.api.Test;

/**
 * Checks snapshot identity preservation and content-based revisions.
 */
public class ResourceSnapshotServiceTests {

    @Test
    public void xmlSnapshotCopiesWithoutMovingObjectsOrLosingIds() throws Exception {
        var original = new XMIResourceImpl(URI.createURI("sirius:///" + UUID.randomUUID()));
        var object = EcoreFactory.eINSTANCE.createEClass();
        object.setName("Customer");
        original.getContents().add(object);
        String objectId = UUID.randomUUID().toString();
        original.setID(object, objectId);
        String content = "{\"name\":\"Customer été\"}";
        var service = new ResourceSnapshotService((resource, migrate) -> {
            assertThat(resource).isInstanceOf(JsonResource.class).isNotSameAs(original);
            assertThat(resource.getURI()).isEqualTo(original.getURI());
            assertThat(resource.getEObject(objectId)).isNotNull().isNotSameAs(object);
            assertThat(migrate).isFalse();
            return Optional.of(new DocumentData(Document.newDocument(UUID.randomUUID()).name("model").content(content).build(), List.of()));
        });

        var snapshot = service.getSnapshot(original).orElseThrow();

        assertThat(snapshot.content()).isEqualTo(content);
        assertThat(snapshot.revision()).isEqualTo(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8))));
        assertThat(object.eResource()).isSameAs(original);
        assertThat(original.getID(object)).isEqualTo(objectId);
    }

    @Test
    public void jsonSnapshotUsesTheCanonicalResourceAndPropagatesMissingSerialization() {
        var original = new JSONResourceFactory().createResource(URI.createURI("sirius:///" + UUID.randomUUID()));
        var service = new ResourceSnapshotService((resource, migrate) -> {
            assertThat(resource).isSameAs(original);
            return Optional.empty();
        });

        assertThat(service.getSnapshot(original)).isEmpty();
    }
}
