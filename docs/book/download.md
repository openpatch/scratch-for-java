---
name: Download
index: 1
---

# Download

## Templates

::archive[BlueJ Starter]{name="bluej-starter"}

::archive[VS Code Starter]{name="vs-code-starter"}

## Scratch for Java Studio (alpha)

![Scratch for Java Studio with the code editor and the visual stage designer side by side](/assets/studio-screenshot.png)

Scratch for Java Studio includes Java, so no separate JDK installation is needed.
Choose the download for your operating system:

- **Windows (64-bit):** [MSI installer](https://github.com/openpatch/scratch-for-java-ide/releases/latest/download/scratch4j-studio-windows-x64.msi) or [portable ZIP](https://github.com/openpatch/scratch-for-java-ide/releases/latest/download/scratch4j-studio-windows-x64-portable.zip)
- **macOS Apple Silicon:** [DMG](https://github.com/openpatch/scratch-for-java-ide/releases/latest/download/scratch4j-studio-macos-arm64.dmg)
- **macOS Intel:** [DMG](https://github.com/openpatch/scratch-for-java-ide/releases/latest/download/scratch4j-studio-macos-x64.dmg)
- **Linux (64-bit):** [AppImage](https://github.com/openpatch/scratch-for-java-ide/releases/latest/download/scratch4j-studio-linux-x64.AppImage) or [DEB package](https://github.com/openpatch/scratch-for-java-ide/releases/latest/download/scratch4j-studio-linux-x64.deb)


## GitHub Releases

This JAR file is platform-independent and can be used on any operating system that supports Java 17.

::download[All Operating Systems]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-all.jar"}

The source code can be downloaded here:

::download[Sources]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-sources.jar"}

If you also need the JavaDoc, i.e., the documentation of the classes, it can be downloaded here:

::download[JavaDoc]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-javadoc.jar"}

It is best to add all three JAR files so that autocomplete and documentation lookup work correctly in your IDE.

### NRW Abitur version

If your course uses the classes of the NRW Zentralabitur, there is a version in
which `find`, `getAll` and `getTouchingSprites` return their `List`. See
[Abiturklassen NRW](/abitur-nrw).

::download[All Operating Systems (NRW)]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-nrw-all.jar"}

::download[Sources (NRW)]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-nrw-sources.jar"}

## Maven Central

```xml
<dependency>
    <groupId>org.openpatch</groupId>
    <artifactId>scratch</artifactId>
    <version>{{VERSION}}</version>
</dependency>
```
