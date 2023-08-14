# Release

## Create release files

```
$newVersion = '0.0.5'
cd .\structurizr-gen\
mvn versions:set "-DgenerateBackupPoms=false" "-DnewVersion=${newVersion}"
git add pom.xml
mvn dependency:tree "-DoutputFile=dependencies-tree.txt"
git add dependencies-tree.txt
mvn dependency:list "-DoutputFile=dependencies-list.txt"
git add dependencies-list.txt
git commit "v${newVersion}"
cd ..
git clone --depth 1 -b main git@github.com:vitalegi/c4-model-arch.git
cd .\c4-model-arch\structurizr-gen\
mvn clean package
cd ..\..\
Copy-Item ".\c4-model-arch\structurizr-gen\target\structurizr-gen-jar-with-dependencies.jar" -Destination "structurizr-gen.jar"
Copy-Item ".\c4-model-arch\structurizr-gen\dependencies-tree.txt" -Destination "dependencies-tree.txt"
Copy-Item ".\c4-model-arch\structurizr-gen\dependencies-list.txt" -Destination "dependencies-list.txt"
```

Files to attach:

- `structurizr-gen.jar`
