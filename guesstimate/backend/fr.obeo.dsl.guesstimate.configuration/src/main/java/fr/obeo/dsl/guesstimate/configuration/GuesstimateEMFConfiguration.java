/*******************************************************************************
 * Copyright (c) 2023, 2026 Obeo.
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

import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.provider.GuesstimateItemProviderAdapterFactory;
import fr.obeo.dsl.guesstimate.util.GuesstimateValidator;

import java.util.Objects;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator.Registry;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configures the Guesstimate metamodel, item providers, and validation.
 *
 * @author cedric
 */
@Configuration
public class GuesstimateEMFConfiguration {

    private final Registry eValidatorRegistry;

    public GuesstimateEMFConfiguration(Registry eValidatorRegistry) {
        this.eValidatorRegistry = Objects.requireNonNull(eValidatorRegistry);
    }

    @Bean
    ComposedAdapterFactory.Descriptor guesstimateAdapterFactoryDescriptor() {
        return GuesstimateItemProviderAdapterFactory::new;
    }

    @Bean
    EPackage guesstimateEPackage() {
        return GuesstimatePackage.eINSTANCE;
    }

    @PostConstruct
    public void registerDomainValidator() {
        this.eValidatorRegistry.put(GuesstimatePackage.eINSTANCE, new GuesstimateValidator());
    }
}
