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

### With Maven

```
mvn exec:java "-Dexec.args=--dsl 123.dsl" -Dexec.mainClass=it.vitalegi.structurizr.gen.Main
```

### With Java

```
java -cp target/structurizr-gen-jar-with-dependencies.jar it.vitalegi.structurizr.gen.Main
```

### Options

