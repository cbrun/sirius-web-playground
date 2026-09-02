/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package fr.obeo.dsl.guesstimate.codegen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/** Keeps generated resources compatible with both Maven and PDE builds. */
final class GeneratedFilesPostProcessor {

    private GeneratedFilesPostProcessor() {
        // Prevent instantiation.
    }

    static void postProcessEditProject(Path project, String defaultSvg) throws IOException {
        mergeAndMirrorProperties(project);
        initializeMissingSvgFiles(project, defaultSvg);
        deleteDirectory(project.resolve("icons"));
    }

    static void mergeAndMirrorProperties(Path project) throws IOException {
        Path generated = project.resolve("plugin.properties");
        Path canonical = project.resolve("src/main/resources/plugin.properties");
        Properties merged = load(generated);
        merged.putAll(load(canonical));

        Set<String> comments = new LinkedHashSet<>();
        if (Files.isRegularFile(canonical)) {
            Files.readAllLines(canonical, StandardCharsets.ISO_8859_1).stream()
                    .filter(line -> line.startsWith("#") || line.startsWith("!"))
                    .forEach(comments::add);
        }

        List<String> lines = new ArrayList<>(comments);
        if (!lines.isEmpty()) {
            lines.add("");
        }
        new TreeSet<>(merged.stringPropertyNames()).stream()
                .map(key -> formatProperty(key, merged.getProperty(key)))
                .forEach(lines::add);
        String content = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        writeIfChanged(canonical, content, StandardCharsets.ISO_8859_1);
        writeIfChanged(generated, content, StandardCharsets.ISO_8859_1);
    }

    private static Properties load(Path path) throws IOException {
        Properties properties = new Properties();
        if (Files.isRegularFile(path)) {
            try (var inputStream = Files.newInputStream(path)) {
                properties.load(inputStream);
            }
        }
        return properties;
    }

    private static String formatProperty(String key, String value) {
        Properties property = new Properties();
        property.setProperty(key, value);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            property.store(outputStream, null);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
        return outputStream.toString(StandardCharsets.ISO_8859_1).lines()
                .filter(line -> !line.startsWith("#") && !line.isBlank())
                .findFirst()
                .orElseThrow();
    }

    private static void writeIfChanged(Path path, String content, java.nio.charset.Charset charset) throws IOException {
        if (!Files.isRegularFile(path) || !Files.readString(path, charset).equals(content)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, charset);
        }
    }

    private static void initializeMissingSvgFiles(Path project, String defaultSvg) throws IOException {
        Path generatedIcons = project.resolve("icons");
        if (!Files.isDirectory(generatedIcons)) {
            return;
        }
        Path resourceIcons = project.resolve("src/main/resources/icons");
        try (Stream<Path> paths = Files.walk(generatedIcons)) {
            for (Path gif : paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".gif"))
                    .toList()) {
                Path relative = generatedIcons.relativize(gif);
                String fileName = relative.getFileName().toString().replaceFirst("\\.gif$", ".svg");
                Path parent = relative.getParent();
                Path svg = resourceIcons.resolve(parent == null ? Path.of(fileName) : parent.resolve(fileName));
                if (!Files.exists(svg)) {
                    Files.createDirectories(svg.getParent());
                    Files.writeString(svg, defaultSvg, StandardCharsets.UTF_8);
                    System.out.println("Initialized missing SVG icon: " + svg);
                }
            }
        }
    }

    static void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
