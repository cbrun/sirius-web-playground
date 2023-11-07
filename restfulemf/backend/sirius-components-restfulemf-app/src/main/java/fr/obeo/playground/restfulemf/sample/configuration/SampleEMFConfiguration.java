/*******************************************************************************
 * Copyright (c) 2019, 2023 Obeo.
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

import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.uml2.types.TypesPackage;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.edit.providers.UMLItemProviderAdapterFactory;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.profile.standard.StandardPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.obeonetwork.dsl.bpmn2.Bpmn2Package;
import org.obeonetwork.dsl.bpmn2.provider.Bpmn2ItemProviderAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.obeo.dsl.designer.sample.flow.FlowPackage;
import fr.obeo.dsl.designer.sample.flow.provider.FlowItemProviderAdapterFactory;

/**
 * Configuration of the EMF support for Sirius Web.
 *
 * @author sbegaudeau
 */
@Configuration
public class SampleEMFConfiguration {
	@Bean
	public AdapterFactory flowAdapterFactory() {
		return new FlowItemProviderAdapterFactory();
	}

	@Bean
	public EPackage flowEPackage() {
		return FlowPackage.eINSTANCE;
	}

	@Bean
	public AdapterFactory bpmnAdapterFactory() {
		return new Bpmn2ItemProviderAdapterFactory();
	}

	@Bean
	public EPackage bpmnEPackage() {
		return Bpmn2Package.eINSTANCE;
	}

	@Bean
	public AdapterFactory umlAdapterFactory() {
		return new UMLItemProviderAdapterFactory();
	}

	@Bean
	public EPackage ecoreEPackage() {
		return EcorePackage.eINSTANCE;
	}

	@Bean
	public EPackage umlEPackage() {
		return UMLPackage.eINSTANCE;
	}

	@Bean
	public EPackage standardEPackage() {
		return StandardPackage.eINSTANCE;
	}

	@Bean
	public EPackage typesEPackage() {
		return TypesPackage.eINSTANCE;
	}

	@Bean
	public Resource.Factory.Registry factoryRegistry() {
		Registry globalFactoryRegistryInstance = Resource.Factory.Registry.INSTANCE;

		// initialize the registry from the global
		Registry factoryRegistry = new ResourceFactoryRegistryImpl();
		Map<String, Object> protocolToFactoryMap = factoryRegistry.getProtocolToFactoryMap();
		globalFactoryRegistryInstance.getProtocolToFactoryMap()
				.forEach((key, value) -> protocolToFactoryMap.put(key, value));
		Map<String, Object> extensionToFactoryMap = factoryRegistry.getExtensionToFactoryMap();
		globalFactoryRegistryInstance.getExtensionToFactoryMap()
				.forEach((key, value) -> extensionToFactoryMap.put(key, value));
		Map<String, Object> contentTypeToFactoryMap = factoryRegistry.getContentTypeToFactoryMap();
		globalFactoryRegistryInstance.getContentTypeToFactoryMap()
				.forEach((key, value) -> contentTypeToFactoryMap.put(key, value));

		// Add factory associated to uml extension
		factoryRegistry.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, new UMLResourceFactoryImpl());

		return factoryRegistry;
	}
}
