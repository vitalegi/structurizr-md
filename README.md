# README

Aim of the project is to build a tool that simplifies the generation/maintenance of [C4 Model](https://c4model.com/) diagrams.

With this in mind, [Structurizr](https://structurizr.com/)'s approach of diagrams and models as code is a good tool for the diagram generation.

What's found to be missing?

- Easy and fast export of all diagrams
- Generation of additional documentation in a portable format (markdown, in our case)
- Auto-generation of containers/components

## Prerequirements

- Java 17
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

There are several way to run the application.

Compile and run with maven

```
mvn clean compile exec:java "-Dexec.args=$arg1 $arg2 ..." "-Dexec.mainClass=$mainClass"
```

Run from the target folder

```
java -cp target/structurizr-md-jar-with-dependencies.jar $mainClass $arg1 $arg2 ...
```

Run the distribution version

```
java -cp structurizr-md.jar $mainClass $arg1 $arg2 ...
```


### Generate images

```
mvn clean compile exec:java "-Dexec.args=sample.dsl sample/md/images/" "-Dexec.mainClass=it.vitalegi.structurizr.md.GenerateImagesApp"
```

Will read configuration from file `sample.dsl` and export the images in `sample/images` folder.

Both values can be replaced with absolute/relative paths.

### Generate Markdown pages

```
mvn clean compile exec:java "-Dexec.args=sample.dsl sample/md/" "-Dexec.mainClass=it.vitalegi.structurizr.md.GenerateMarkdownApp"
```

#### Params

| Position | Mandatory | Default value | Description                                                                                    |
| -------- | --------- | ------------- |------------------------------------------------------------------------------------------------|
| 1        | Yes       | N/A           | Path to the DSL file                                                                           |
| 2        | Yes       | N/A           | Folder where to create the generated content                                                   |
| 3        | No        | true          | true &rarr; generate all default views. false &rarr; use only the views available in the model |

## Contributions

Thanks, but no.

## Bugs

Keep them. 

## Support

You are on your own.
