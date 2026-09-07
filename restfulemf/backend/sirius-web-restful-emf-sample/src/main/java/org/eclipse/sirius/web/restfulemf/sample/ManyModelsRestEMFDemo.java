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
package org.eclipse.sirius.web.restfulemf.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.sirius.web.restfulemf.client.RestfulEMFClient;

/**
 * Demonstrates how a plain EMF client can load a Sirius Web project and save a document.
 */
public class ManyModelsRestEMFDemo {

    private static final String CREATED_PACKAGE_NAME = "Created from The Client code";

    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    private final Logger logger = LoggerFactory.getLogger(ManyModelsRestEMFDemo.class);

    public static void main(String[] args) throws IOException {
        new ManyModelsRestEMFDemo().run();
    }

    private void run() throws IOException {
        long demoStartedAt = this.startStep("demo");
        long stepStartedAt = this.startStep("project-url-input");
        URI projectURI = this.readProjectURI();
        this.completeStep("project-url-input", stepStartedAt);

        stepStartedAt = this.startStep("initial-project-load");
        Resource resource = this.load(projectURI);
        this.completeStep("initial-project-load", stepStartedAt);

        stepStartedAt = this.startStep("local-model-update");
        Model model = this.getModel(resource);
        var createdPackage = UMLFactory.eINSTANCE.createPackage();
        createdPackage.setName(CREATED_PACKAGE_NAME);
        model.getPackagedElements().add(0, createdPackage);
        this.completeStep("local-model-update", stepStartedAt);

        stepStartedAt = this.startStep("user-pause");
        this.waitBeforeSave();
        this.completeStep("user-pause", stepStartedAt);

        stepStartedAt = this.startStep("resource-save");
        resource.save(Map.of());
        this.completeStep("resource-save", stepStartedAt);

        stepStartedAt = this.startStep("resource-reload");
        resource.unload();
        resource.load(Map.of());
        this.completeStep("resource-reload", stepStartedAt);

        stepStartedAt = this.startStep("persisted-model-validation");
        Model reloadedModel = this.getModel(resource);
        if (reloadedModel.getPackagedElements().isEmpty() || !CREATED_PACKAGE_NAME.equals(reloadedModel.getPackagedElements().get(0).getName())) {
            throw new IllegalStateException("The package was not persisted at the first position");
        }
        this.completeStep("persisted-model-validation", stepStartedAt);
        this.completeStep("demo", demoStartedAt);
        System.out.println("The package was created and persisted in " + resource.getURI() + '.');
    }

    private long startStep(String step) {
        this.logger.atInfo()
                .setMessage("RESTful EMF demo step {} started")
                .addArgument(step)
                .addKeyValue("step", step)
                .log();
        return System.nanoTime();
    }

    private void completeStep(String step, long startedAt) {
        long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        this.logger.atInfo()
                .setMessage("RESTful EMF demo step {} completed in {} ms")
                .addArgument(step)
                .addArgument(duration)
                .addKeyValue("step", step)
                .addKeyValue("durationMs", duration)
                .log();
    }

    private URI readProjectURI() throws IOException {
        System.out.print("Crée un projet \"Many Models\", puis colle l'URL du projet ici : ");
        String projectURL = this.reader.readLine();
        if (projectURL == null || projectURL.isBlank()) {
            throw new IllegalArgumentException("A Sirius Web project URL is required");
        }

        return URI.createURI(projectURL.trim());
    }

    private void waitBeforeSave() throws IOException {
        System.out.println("Le modèle est chargé et modifié localement.");
        System.out.print("Appuie sur Entrée pour sauvegarder, ou modifie d'abord le modèle dans Sirius Web pour tester le conflit : ");
        if (this.reader.readLine() == null) {
            throw new IOException("Standard input was closed before the save");
        }
    }

    private Resource load(URI projectURI) throws IOException {
        var resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        new RestfulEMFClient().loadProject(projectURI, resourceSet);
        return resourceSet.getResources().stream()
                .filter(resource -> resource.getContents().size() == 1 && resource.getContents().get(0) instanceof Model)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("The project does not contain a UML model document"));
    }

    private Model getModel(Resource resource) {
        if (resource.getContents().size() == 1 && resource.getContents().get(0) instanceof Model model) {
            return model;
        }
        throw new IllegalStateException("The document does not contain one UML model root");
    }
}
