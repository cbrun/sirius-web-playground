/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.obeo.dsl.guesstimate.codegen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/** Generates the Guesstimate EMF model and edit code outside Eclipse. */
public final class GuesstimateEMFGenerator {

    private static final String MODEL_PROJECT_NAME = "fr.obeo.dsl.guesstimate";

    private static final String EDIT_PROJECT_NAME = "fr.obeo.dsl.guesstimate.edit";

    private GuesstimateEMFGenerator() {
        // Prevent instantiation.
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected one argument: <guesstimate-backend-directory>");
        }

        Path backendDirectory = Path.of(args[0]).toAbsolutePath().normalize();
        Path modelProject = backendDirectory.resolve(MODEL_PROJECT_NAME);
        Path editProject = backendDirectory.resolve(EDIT_PROJECT_NAME);
        Path genmodelPath = modelProject.resolve("model/guesstimate.genmodel");
        if (!Files.isRegularFile(genmodelPath) || !Files.isDirectory(editProject)) {
            throw new IllegalArgumentException("Invalid Guesstimate backend directory: " + backendDirectory);
        }

        ResourceSet resourceSet = createResourceSet(modelProject, editProject);
        Resource resource = resourceSet.getResource(URI.createFileURI(genmodelPath.toString()), true);
        EcoreUtil.resolveAll(resourceSet);
        validate(resourceSet);

        GenModel genModel = resource.getContents().stream()
                .filter(GenModel.class::isInstance)
                .map(GenModel.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No GenModel found in " + genmodelPath));

        Generator generator = createGenerator(resourceSet, genModel);
        String defaultSvg = loadDefaultSvg();
        generate(generator, genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, "MODEL");
        GeneratedFilesPostProcessor.mergeAndMirrorProperties(modelProject);
        GeneratedFilesPostProcessor.deleteDirectory(editProject.resolve("icons"));
        generate(generator, genModel, GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE, "EDIT");
        GeneratedFilesPostProcessor.postProcessEditProject(editProject, defaultSvg);
    }

    private static ResourceSet createResourceSet(Path modelProject, Path editProject) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
        resourceSet.getPackageRegistry().put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        resourceSet.getURIConverter().getURIMap().put(
                URI.createURI("platform:/plugin/org.eclipse.emf.ecore/model/Ecore.ecore"),
                URI.createURI(EcorePackage.eNS_URI));
        registerProject(resourceSet, MODEL_PROJECT_NAME, modelProject);
        registerProject(resourceSet, EDIT_PROJECT_NAME, editProject);
        return resourceSet;
    }

    private static void registerProject(ResourceSet resourceSet, String projectName, Path projectPath) {
        resourceSet.getURIConverter().getURIMap().put(
                URI.createURI("platform:/resource/" + projectName + "/"),
                URI.createFileURI(projectPath.toString() + "/"));
    }

    private static void validate(ResourceSet resourceSet) {
        for (Resource resource : List.copyOf(resourceSet.getResources())) {
            for (EObject root : resource.getContents()) {
                Diagnostic diagnostic = Diagnostician.INSTANCE.validate(root);
                if (diagnostic.getSeverity() == Diagnostic.ERROR) {
                    throw new IllegalStateException("Invalid EMF resource " + resource.getURI() + ": " + diagnostic);
                }
            }
        }
    }

    private static Generator createGenerator(ResourceSet resourceSet, GenModel genModel) {
        genModel.reconcile();
        genModel.setCanGenerate(true);
        // Eclipse-only formatting is deliberately disabled for reproducible headless output.
        genModel.setCodeFormatting(false);
        genModel.setCommentFormatting(false);
        genModel.setCleanup(true);

        Generator generator = new Generator();
        generator.getAdapterFactoryDescriptorRegistry().addDescriptor(
                GenModelPackage.eNS_URI,
                GenModelGeneratorAdapterFactory.DESCRIPTOR);
        generator.setInput(genModel);
        Generator.Options options = generator.getOptions();
        options.codeFormatting = false;
        options.commentFormatting = false;
        options.importOrganizing = false;
        options.cleanup = true;
        options.resourceSet = resourceSet;
        return generator;
    }

    private static void generate(Generator generator, GenModel genModel, String projectType, String label) {
        System.out.println("Generating Guesstimate " + label + " code");
        Diagnostic diagnostic = generator.generate(genModel, projectType, new BasicMonitor.Printing(System.out));
        if (diagnostic.getSeverity() == Diagnostic.ERROR || diagnostic.getSeverity() == Diagnostic.CANCEL) {
            throw new IllegalStateException(label + " generation failed: " + diagnostic);
        }
    }

    private static String loadDefaultSvg() throws IOException {
        try (var inputStream = GuesstimateEMFGenerator.class.getResourceAsStream("/Default.svg")) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing /Default.svg");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
