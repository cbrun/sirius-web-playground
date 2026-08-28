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
package fr.obeo.playground.restfulemf.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Demonstrates how a plain EMF client can load and save a Sirius Web document.
 */
public class ManyModelsRestEMFDemo {

    private static final String CREATED_PACKAGE_NAME = "Created from The Client code";

    private static final Map<String, Object> OPTIONS = Map.of(XMLResource.OPTION_BINARY, Boolean.TRUE);

    public static void main(String[] args) throws IOException {
        new ManyModelsRestEMFDemo().run();
    }

    private void run() throws IOException {
        URI modelURI = this.readModelURI();
        Resource resource = this.load(modelURI);
        Model model = this.getModel(resource);

        var createdPackage = UMLFactory.eINSTANCE.createPackage();
        createdPackage.setName(CREATED_PACKAGE_NAME);
        model.getPackagedElements().add(0, createdPackage);
        resource.save(OPTIONS);

        resource.unload();
        resource.load(OPTIONS);
        Model reloadedModel = this.getModel(resource);
        if (reloadedModel.getPackagedElements().isEmpty() || !CREATED_PACKAGE_NAME.equals(reloadedModel.getPackagedElements().get(0).getName())) {
            throw new IllegalStateException("The package was not persisted at the first position");
        }
        System.out.println("The package was created and persisted in linux-kernel.uml.");
    }

    private URI readModelURI() throws IOException {
        System.out.print("Crée un projet \"Many Models\", puis colle l'URL du projet ici : ");
        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String projectURL = reader.readLine();
        if (projectURL == null || projectURL.isBlank()) {
            throw new IllegalArgumentException("A Sirius Web project URL is required");
        }

        URI projectURI = URI.createURI(projectURL.trim());
        String projectId = this.getProjectId(projectURI);
        return URI.createHierarchicalURI(projectURI.scheme(), projectURI.authority(), null,
                new String[] { "api", "rest", "projects", projectId, "linux-kernel.uml", "bin" }, null, null);
    }

    private String getProjectId(URI projectURI) {
        if (!("http".equals(projectURI.scheme()) || "https".equals(projectURI.scheme())) || projectURI.authority() == null) {
            throw new IllegalArgumentException("The project URL must use HTTP or HTTPS");
        }
        for (int index = 0; index < projectURI.segmentCount() - 1; index++) {
            if ("projects".equals(projectURI.segment(index))) {
                String projectId = projectURI.segment(index + 1);
                UUID.fromString(projectId);
                return projectId;
            }
        }
        throw new IllegalArgumentException("The URL does not contain a Sirius Web project identifier");
    }

    private Resource load(URI modelURI) throws IOException {
        var resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        var resource = new XMLResourceImpl(modelURI);
        resourceSet.getResources().add(resource);
        resource.load(OPTIONS);
        return resource;
    }

    private Model getModel(Resource resource) {
        if (resource.getContents().size() == 1 && resource.getContents().get(0) instanceof Model model) {
            return model;
        }
        throw new IllegalStateException("linux-kernel.uml does not contain one UML model root");
    }
}
