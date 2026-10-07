---
name: Erste Schritte
---

# Von Scratch zu Java

Eine Bühne heißt **Stage**, eine Figur **Sprite** und ein Aussehen **costume**.
Wir beginnen mit derselben Figur wie im englischen ersten Tutorial.

## Drei Einstiege

1. **Im Browser ausprobieren:** Öffne [Your first program](/tutorials/getting-started)
   und drücke ▶. Ändere den Kostümnamen oder eine Zahl und starte erneut.
2. **Mit Studio arbeiten:** [Studio herunterladen](/download), ein Projekt
   erstellen, `MyStage` öffnen und die Figur hinzufügen. Java und die Bibliothek
   sind enthalten; du brauchst kein zusätzliches JDK.
3. **In deiner Java-Umgebung:** Die Bibliothek nach [Setup](/setup) einbinden.
   Normale Klassen laufen ab Java 17. Eine kompakte Datei mit `void main()`
   benötigt Java 25; Studio unterstützt diese Form.

## Das erste Programm

Lege `MyStage.java` an:

    import org.openpatch.scratch.*;

    public class MyStage extends Stage {
      public MyStage() {
        Sprite bunny = new Sprite();
        bunny.addCostume("bunny1_stand");
        this.add(bunny);
      }
      public static void main(String[] args) {
        new MyStage();
      }
    }

In Studio drückst du ▶. In BlueJ kannst du auch `new MyStage()` wählen.
Die Figur benötigt keine eigene Bilddatei. Mit `this.add(bunny)` wird sie
auf die Bühne gesetzt. Ihr späteres `run()` wird einmal pro Spielbild aufgerufen.

## Speichern und wechseln

Browserdaten bleiben in diesem Browser auf diesem Rechner. Sichere eine
Workspace-JSON oder, bei einer IDE mit ZIP-Unterstützung, eine Projekt-ZIP.
Studio öffnet beide über das Projektmenü und exportiert eine Browser-Projekt-ZIP.
Bewahre Java-Dateien und eigene Bilder, Klänge und Schriften zusammen auf.

Wählt euer Kurs **NRW**, bleibt bei dieser Auswahl. Auf dem Desktop gehören
auch die Abiturklassen wie `List.java` zum Projekt; im Browser sind sie vorhanden.
Ein Spielwerkstatt-Checkpoint bringt diese Dateien mit.

## Weiterlernen

Die [Spielwerkstatt in Hyperbook Informatik](https://informatik.openpatch.org/projekte/spielwerkstatt)
verbindet die Java-Lernpfade mit eigenen Spielideen, Checkpoints und einem
Entwicklertagebuch. Begründe darin deine Entscheidungen und teste nach jedem
Wechsel der Umgebung. Weitere Aufgaben stehen in den [sieben Tutorials](/tutorials)
und im [Kursmaterial](/classroom).
