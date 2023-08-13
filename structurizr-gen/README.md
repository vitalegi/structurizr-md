# README

## Prerequirements

- Java 11
- Maven
- (TBV) <https://graphviz.org/>

```
$env:PATH="C:\a\software\graphviz-8.1.0-win32\Graphviz\bin;${env:PATH}"
```

## Build

```
mvn clean package
```

## Run

### Generate images

```
java -cp target/structurizr-gen-jar-with-dependencies.jar it.vitalegi.structurizr.gen.GenerateImagesApp sample.dsl sample/md/images/
```

```
mvn clean compile exec:java "-Dexec.args=sample.dsl sample/md/images/" "-Dexec.mainClass=it.vitalegi.structurizr.gen.GenerateImagesApp"
```

Both commands will read configuration from file `sample.dsl` and export the images in `sample/images` folder. Both
values can be replaced with absolute/relative paths.

### Generate Markdown pages

```
java -cp target/structurizr-gen-jar-with-dependencies.jar it.vitalegi.structurizr.gen.GenerateMarkdownApp sample.dsl sample/md/
```

```
mvn clean compile exec:java "-Dexec.args=sample.dsl sample/md/" "-Dexec.mainClass=it.vitalegi.structurizr.gen.GenerateMarkdownApp"
```

#### Params

| Position | Mandatory | Default value | Description                                                                         |
| -------- | --------- | ------------- | ----------------------------------------------------------------------------------- |
| 1        | Yes       | N/A           | Path to the DSL file                                                                |
| 2        | Yes       | N/A           | Folder where to create the generated content                                        |
| 3        | No        | true          | true &rarr; generate also the images. false &rarr; assume the images already exists |
