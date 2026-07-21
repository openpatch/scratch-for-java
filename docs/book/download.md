---
name: Download
index: 1
---

# Download

## Templates

::archive[BlueJ Starter]{name="bluej-starter"}

::archive[VS Code Starter]{name="vs-code-starter"}


## GitHub Releases

This JAR file is platform-independent and can be used on any operating system that supports Java 17.

::download[All Operating Systems]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-all.jar"}

The source code can be downloaded here:

::download[Sources]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-sources.jar"}

If you also need the JavaDoc, i.e., the documentation of the classes, it can be downloaded here:

::download[JavaDoc]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-javadoc.jar"}

It is best to add all three JAR files so that autocomplete and documentation lookup work correctly in your IDE.

## Maven Central

```xml
<dependency>
    <groupId>org.openpatch</groupId>
    <artifactId>scratch</artifactId>
    <version>{{VERSION}}</version>
</dependency>
```
