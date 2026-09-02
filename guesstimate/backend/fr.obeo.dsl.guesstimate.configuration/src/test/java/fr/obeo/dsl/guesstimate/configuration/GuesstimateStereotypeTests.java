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

import static org.assertj.core.api.Assertions.assertThat;

import fr.obeo.dsl.guesstimate.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.web.application.document.dto.Stereotype;
import org.eclipse.sirius.web.application.editingcontext.EditingContext;
import org.eclipse.sirius.web.application.views.explorer.services.ExplorerDescriptionProvider;
import org.junit.jupiter.api.Test;

/**
 * Tests the Guesstimate document type contribution.
 *
 * @author cedric
 */
public class GuesstimateStereotypeTests {

    @Test
    public void givenAnEMFEditingContextWhenCreatingAGuesstimateDocumentThenASheetIsCreated() {
        var resourceSet = new ResourceSetImpl();
        var adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
        var editingDomain = new AdapterFactoryEditingDomain(adapterFactory, new BasicCommandStack(), resourceSet);
        var editingContext = new EditingContext("editingContextId", editingDomain, new HashMap<>(), new ArrayList<>());

        var stereotypeProvider = new GuesstimateStereotypeProvider();
        var stereotypeHandler = new GuesstimateStereotypeHandler(List.of());
        var optionalDocument = stereotypeHandler.handle(editingContext, GuesstimateStereotypeProvider.GUESSTIMATE_STEREOTYPE_ID, "My estimates");

        assertThat(stereotypeProvider.getStereotypes(editingContext)).containsExactly(new Stereotype("guesstimate", "Guesstimate"));
        assertThat(optionalDocument).isPresent().get()
                .satisfies(document -> {
                    assertThat(document.name()).isEqualTo("My estimates");
                    assertThat(document.kind()).isEqualTo(ExplorerDescriptionProvider.DOCUMENT_KIND);
                });
        assertThat(resourceSet.getResources()).singleElement()
                .satisfies(resource -> {
                    assertThat(resource.getContents()).singleElement().isInstanceOf(Sheet.class);
                    assertThat(resource.eAdapters()).filteredOn(ResourceMetadataAdapter.class::isInstance).singleElement()
                            .extracting(ResourceMetadataAdapter.class::cast)
                            .satisfies(adapter -> {
                                assertThat(adapter.getName()).isEqualTo("My estimates");
                                assertThat(adapter.getLastMigrationData()).isNotNull();
                            });
                });

        editingContext.dispose();
    }
}
