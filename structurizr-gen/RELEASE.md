# Release

## Create release files

```
mvn versions:set "-DgenerateBackupPoms=false" "-DnewVersion=0.0.3"
git clone --depth 1 -b main git@github.com:vitalegi/c4-model-arch.git
cd .\c4-model-arch\structurizr-gen\
mvn clean package
cd ..\..\
Copy-Item ".\c4-model-arch\structurizr-gen\target\structurizr-gen-jar-with-dependencies.jar" -Destination "structurizr-gen.jar"
```

Files to attach:

- `structurizr-gen.jar`
