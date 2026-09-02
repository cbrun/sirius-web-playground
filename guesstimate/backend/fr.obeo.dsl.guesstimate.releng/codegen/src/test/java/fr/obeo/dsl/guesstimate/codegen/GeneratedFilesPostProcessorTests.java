/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.obeo.dsl.guesstimate.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GeneratedFilesPostProcessorTests {

    @Test
    void shouldMergePropertiesAndInitializeOnlyMissingSvgFiles(@TempDir Path project) throws Exception {
        Path canonicalProperties = project.resolve("src/main/resources/plugin.properties");
        Files.createDirectories(canonicalProperties.getParent());
        Files.writeString(canonicalProperties, "# Custom values\nshared=custom\n");
        Files.writeString(project.resolve("plugin.properties"), "generated=value\nshared=generated\nunicode=Mod\\u00e8le\n");

        Path generatedIcons = project.resolve("icons/full/obj16");
        Path resourceIcons = project.resolve("src/main/resources/icons/full/obj16");
        Files.createDirectories(generatedIcons);
        Files.createDirectories(resourceIcons);
        Files.writeString(generatedIcons.resolve("Existing.gif"), "gif");
        Files.writeString(generatedIcons.resolve("Missing.gif"), "gif");
        Files.writeString(resourceIcons.resolve("Existing.svg"), "custom-svg");

        GeneratedFilesPostProcessor.postProcessEditProject(project, "default-svg");
        String firstResult = Files.readString(canonicalProperties);
        GeneratedFilesPostProcessor.postProcessEditProject(project, "default-svg");

        assertThat(firstResult)
                .isEqualTo(Files.readString(project.resolve("plugin.properties")))
                .isEqualTo(Files.readString(canonicalProperties))
                .contains("# Custom values", "generated=value", "shared=custom", "unicode=Mod\\u00E8le");
        assertThat(Files.readString(resourceIcons.resolve("Existing.svg"))).isEqualTo("custom-svg");
        assertThat(Files.readString(resourceIcons.resolve("Missing.svg"))).isEqualTo("default-svg");
        assertThat(project.resolve("icons")).doesNotExist();
    }
}
