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

import fr.obeo.dsl.guesstimate.GuesstimatePackage;

import java.util.List;
import java.util.Objects;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.sirius.components.collaborative.forms.services.api.IPropertiesDescriptionRegistry;
import org.eclipse.sirius.components.collaborative.forms.services.api.IPropertiesDescriptionRegistryConfigurer;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;
import org.eclipse.sirius.components.view.emf.form.ViewFormDescriptionConverter;
import org.eclipse.sirius.components.view.form.FormDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the Guesstimate form descriptions as Sirius Web properties descriptions.
 *
 * @author cedric
 */
@Configuration
public class GuesstimatePropertiesConfigurer implements IPropertiesDescriptionRegistryConfigurer {

    public static final String GUESSTIMATE_DETAIL_VIEW_NAME = "Guesstimate Detail View";

    private final ViewFormDescriptionConverter converter;

    private final View guesstimateView;

    private final List<IJavaServiceProvider> javaServiceProviders;

    private final ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(GuesstimatePropertiesConfigurer.class);

    public GuesstimatePropertiesConfigurer(ViewFormDescriptionConverter converter, @Qualifier("guesstimateView") View guesstimateView,
            ApplicationContext applicationContext, List<IJavaServiceProvider> javaServiceProviders) {
        this.converter = Objects.requireNonNull(converter);
        this.guesstimateView = Objects.requireNonNull(guesstimateView);
        this.javaServiceProviders = Objects.requireNonNull(javaServiceProviders);
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    public void addPropertiesDescriptions(IPropertiesDescriptionRegistry registry) {
        AQLInterpreter interpreter = this.createInterpreter(this.guesstimateView, List.of(GuesstimatePackage.eINSTANCE));
        this.guesstimateView.getDescriptions().stream()
                .filter(FormDescription.class::isInstance)
                .map(FormDescription.class::cast)
                .forEach(formDescription -> this.register(formDescription, interpreter, registry));
    }

    private AQLInterpreter createInterpreter(View view, List<EPackage> visibleEPackages) {
        AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
        List<Object> serviceInstances = this.javaServiceProviders.stream()
                .flatMap(provider -> provider.getServiceClasses(view).stream())
                .map(serviceClass -> {
                    try {
                        return beanFactory.createBean(serviceClass);
                    } catch (BeansException beansException) {
                        this.logger.atWarn()
                                .setMessage("The Java service class {} could not be instantiated")
                                .addArgument(serviceClass.getName())
                                .setCause(beansException)
                                .log();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(Object.class::cast)
                .toList();
        return new AQLInterpreter(List.of(), serviceInstances, visibleEPackages);
    }

    private void register(FormDescription viewFormDescription, AQLInterpreter interpreter, IPropertiesDescriptionRegistry registry) {
        var converted = this.converter.convert(viewFormDescription, List.of(), interpreter).representationDescription();
        if (converted instanceof org.eclipse.sirius.components.forms.description.FormDescription formDescription) {
            formDescription.getPageDescriptions().forEach(registry::add);
        }
    }
}
