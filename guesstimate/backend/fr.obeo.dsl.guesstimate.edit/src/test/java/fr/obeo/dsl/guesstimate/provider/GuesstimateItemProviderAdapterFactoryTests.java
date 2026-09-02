/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.obeo.dsl.guesstimate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import fr.obeo.dsl.guesstimate.GuesstimateFactory;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.junit.jupiter.api.Test;

class GuesstimateItemProviderAdapterFactoryTests {

    @Test
    void shouldProvideAResolvableImageForEveryConcreteModelType() {
        var adapterFactory = new GuesstimateItemProviderAdapterFactory();

        GuesstimatePackage.eINSTANCE.getEClassifiers().stream()
                .filter(EClass.class::isInstance)
                .map(EClass.class::cast)
                .filter(eClass -> !eClass.isAbstract() && !eClass.isInterface())
                .forEach(eClass -> {
                    var object = GuesstimateFactory.eINSTANCE.create(eClass);
                    var labelProvider = (IItemLabelProvider) adapterFactory.adapt(object, IItemLabelProvider.class);
                    assertThat(labelProvider).as(eClass.getName()).isNotNull();
                    assertThat(labelProvider.getImage(object)).as(eClass.getName()).isNotNull();
                });

        adapterFactory.dispose();
    }
}
