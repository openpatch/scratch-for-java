---
name: Setup
lang: de
index: 2
---

# Setup

## VS Code (Empfohlen)

Stelle zunächst sicher, dass du das Java Extension Pack installiert hast. Du findest es in der Erweiterungsansicht, indem du nach `redhat.java` suchst.

Dann musst du die Scratch for Java jar für dein [Betriebssystem](/download) herunterladen.

Anschließend musst du die jar zu deinem Projekt hinzufügen. Du kannst dies tun, indem du Folgendes zu deiner `settings.json` hinzufügst:

```json
"java.project.referencedLibraries": [
    "path/to/scratch4j.jar"
]
```

Wenn du mit BlueJ kompatibel sein möchtest, solltest du einen "+libs"-Ordner in deinem Projekt verwenden, die jar dort hinzufügen und dann Folgendes zu deiner `settings.json` hinzufügen:

```json
"java.project.referencedLibraries": [
    "+libs/*.jar"
]
```

## BlueJ

Du kannst die Bibliothek auf drei verschiedene Arten installieren.

Als Erstes musst du dir die Scratch for Java jar-Datei für dein [Betriebssystem](/download) herunterladen.

### Für ein einzelnes Projekt

Erstelle einen Ordner "+libs" in deinem BlueJ-Projektordner. Kopiere die Scratch for Java jar in den Ordner "+libs". Starte BlueJ neu. Jetzt ist Scratch for Java für dieses Projekt verfügbar.

### Benutzereinstellungen

Öffne den Einstellungsdialog und wähle den Reiter Bibliotheken. Dann fügst du eine neue Datei hinzu. Starte BlueJ neu. Jetzt ist Scratch for Java für alle deine Projekte verfügbar.

### Für das ganze System

Dafür musst du den Ordner "userlib" finden. Er befindet sich im BlueJ-Ordner unter `lib/userlib`. Anschließend kopierst du die Scratch for Java Jar in diesen Ordner. Starte BlueJ nue. Jetzt ist Scratch for Java für alle Benutzer deines Computers verfügbar.

Wenn du BlueJ installiert hast, findest du den "userlib"-Ordner hier:

- Windows: `C:\Program Files\BlueJ\lib\userlib`
- MacOS: `/Application/BlueJ/BlueJ.app/Resources/java/userlib`
- Linux: `/usr/share/bluej/userlib/`

## Eigenständig

Du kannst die Jar-Datei auch deinme classpath hinzufügen und so unabhängig von BlueJ mit deinem Lieblingseditor verwenden.

