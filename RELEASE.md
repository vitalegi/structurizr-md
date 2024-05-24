# Release

## Create release files

```
$newVersion = '0.0.9'
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

## Create release

- <https://github.com/vitalegi/structurizr-md/releases>
- Draft a new release
- Choose a tag 'v0.0.x'
- Title 'v0.0.x'
- Generate release notes

Files to attach:

- `structurizr-md.jar`
- `dependencies-tree.txt`
- `dependencies-list.txt`

Set as the latest release and Publish release