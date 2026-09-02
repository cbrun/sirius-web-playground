# Guesstimate EMF code generation

Run the generator explicitly from the repository root:

```text
mvn process-classes -f guesstimate/backend/fr.obeo.dsl.guesstimate.releng/codegen/pom.xml
```

The command validates `guesstimate.genmodel`, regenerates the model and edit
sources, merges generated properties into the Maven resources, keeps the PDE
properties in sync, and creates placeholder SVG files for missing item-provider
icons. Existing SVG files and methods marked `@generated NOT` are preserved.
