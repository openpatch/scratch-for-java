---
name: Download
index: 1
---

# Download

## GitHub Releases

Dieses Jar-Datei ist plattformunabhängig und kann auf jedem Betriebssystem verwendet werden, das Java 17 unterstützt.

::download[Alle Betriebssysteme]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{VERSION}}-all.jar"}

Die Quelltexte können hier heruntergeladen werden:

::download[Sources]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{version}}-sources.jar"}

Wenn auch die JavaDoc, also die Dokumentation der Klassen, benötigt wird, kann diese hier heruntergeladen werden:

::download[JavaDoc]{src="https://github.com/openpatch/scratch-for-java/releases/latest/download/scratch-{{version}}-javadoc.jar"}

Am Besten fügt man alle drei Jar-Dateien hinzu, damit die Autovervollständigung und das nachschauen in der Dokumentation in der IDE funktioniert.

## Maven Central

```xml
<dependency>
    <groupId>org.openpatch</groupId>
    <artifactId>scratch</artifactId>
    <version>{{VERSION}}</version>
</dependency>
```
