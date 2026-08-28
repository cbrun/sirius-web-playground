/*******************************************************************************
 * Copyright (c) 2019, 2026 Obeo.
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
package fr.obeo.playground.restfulemf.sample.configuration;

import fr.obeo.dsl.designer.sample.flow.FlowPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.edit.providers.UMLItemProviderAdapterFactory;
import org.obeonetwork.dsl.bpmn2.Bpmn2Package;
import org.obeonetwork.dsl.bpmn2.provider.Bpmn2ItemProviderAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Ecore metamodel used by the sample.
 */
@Configuration
public class SampleEMFConfiguration {

    @Bean
    public EPackage ecoreEPackage() {
        return EcorePackage.eINSTANCE;
    }

    @Bean
    public EPackage flowEPackage() {
        return FlowPackage.eINSTANCE;
    }

    @Bean
    public EPackage bpmnEPackage() {
        return Bpmn2Package.eINSTANCE;
    }

    @Bean
    public EPackage umlEPackage() {
        return UMLPackage.eINSTANCE;
    }

    @Bean
    public AdapterFactory ecoreAdapterFactory() {
        return new EcoreItemProviderAdapterFactory();
    }

    @Bean
    public AdapterFactory bpmnAdapterFactory() {
        return new Bpmn2ItemProviderAdapterFactory();
    }

    @Bean
    public AdapterFactory umlAdapterFactory() {
        return new UMLItemProviderAdapterFactory();
    }
}
