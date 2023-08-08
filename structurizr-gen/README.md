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
java -cp target/structurizr-gen-jar-with-dependencies.jar it.vitalegi.structurizr.gen.Main --dsl sample.dsl --o RAW --out-dir-png sample/images/ --out-dir-svg sample/images/
```

```
mvn clean compile exec:java "-Dexec.args=--dsl sample.dsl --o RAW --out-dir-png sample/images/ --out-dir-svg sample/images/" "-Dexec.mainClass=it.vitalegi.structurizr.gen.Main"
```

### Generate BitBucket pages

```
java -cp target/structurizr-gen-jar-with-dependencies.jar it.vitalegi.structurizr.gen.GenerateMarkdownApp sample.dsl example/md/
```

```
mvn clean compile exec:java "-Dexec.args=sample.dsl example/md/" "-Dexec.mainClass=it.vitalegi.structurizr.gen.GenerateMarkdownApp"
```
