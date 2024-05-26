# README

## Simple webapp

```
mvn clean compile exec:java "-Dexec.args=examples/simple-webapp.dsl examples/single-webapp" "-Dexec.mainClass=it.vitalegi.structurizr.md.GenerateImagesApp"
```

## Full Example

```
mvn clean compile exec:java "-Dexec.args=examples/full-example.dsl examples/full-example/md/ true" "-Dexec.mainClass=it.vitalegi.structurizr.md.GenerateMarkdownApp"
```

## Multiple Entrypoints

```
mvn clean compile exec:java "-Dexec.args=examples/multiple-entrypoints.dsl examples/multiple-entrypoints" "-Dexec.mainClass=it.vitalegi.structurizr.md.GenerateImagesApp"
```