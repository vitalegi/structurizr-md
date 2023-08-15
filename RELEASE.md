# Release

## Create release files

```
$newVersion = '0.0.7'
mvn versions:set "-DgenerateBackupPoms=false" "-DnewVersion=${newVersion}"
git add pom.xml
mvn dependency:tree "-DoutputFile=dependencies-tree.txt"
git add dependencies-tree.txt
mvn dependency:list "-DoutputFile=dependencies-list.txt"
git add dependencies-list.txt
git commit -m "v${newVersion}"
mvn clean package
Copy-Item ".\target\structurizr-md-jar-with-dependencies.jar" -Destination "structurizr-md.jar"
```

Files to attach:

- `structurizr-gen.jar`
