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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
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

}
