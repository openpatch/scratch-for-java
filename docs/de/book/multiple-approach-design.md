---
name: Design mit mehreren Ansätzen
lang: de
---

# Design mit mehreren Ansätzen

Scratch for Java wurde so implementiert, dass mehrere didaktische Ansätze und Herangehensweisen an die Programmierung mit Java unterstützt werden. So kannst du den Ansatz wählen, der am besten zur dir und deiner Lerngruppe passt.

## Imperativer Ansatz (Klassen-später)

Der imperative Ansatz nutzt nur die Klassen Stage (Bühne) und Sprite (Figur) der Bibliothek. Diese beiden Klassen sind den Schüler:innen bereits durch Scratch bekannt. Die Methoden der beiden Klassen sind den Scratch-Blöcken nachempfungen, sodass auch hier die Schüler:innen viel von ihrem Wissen von Scratch nutzen können.

So können die Schüler:innen ihr erstes einfaches Java-Programm schreiben, ohne mit eigenen Klassen arbeiten zu müssen und können auf algorithmische Muster, die ihnen von Scratch bekannt sind zurückgreifen.

Der Nachteil ist, dass das Verhalten der Sprite-Objekte in einer globalen Klasse definiert wird und nicht wie in Scratch für jede Figur (Sprite) individuell.

Das heißt, dass mit diesem Ansatz die Einfachheit im Umgang mit Java gegen Kompatibilität mit dem Scratch-Modell getauscht wird.

```java
import org.openpatch.scratch.*;

public class MyProgram {
    public MyProgram() {
        Stage myStage = new Stage();
        Sprite zebra = new Sprite();
        zebra.addCostume("walk_1", "assets/walk_1.png");
        zebra.setOnEdgeBounce(true);

        zebra.setRun(s -> {
            s.move(1);
        })
    }
}
```

## Objektorientierte Ansatz (aka. Klassen-zuerst)

Dieser Ansatz ist dem Scratch-Modell am ähnlichsten. Um dies zu erreichen, muss direkt mit Vererbung gearbeitet werden. Auch wenn man jetzt vielleicht denkt, dass dieser Ansatz am Anfang zu komplex für Schüler:innen sein könnte, kann man durch den direkten Vergleich von Scratch-Programmen und den Java-Programmen den Übergang gut moderieren.

Hier sind ein paar Vergleiche, die man verwenden könnte:
- In Scratch haben wir und Projekt vorbereitet indem wir viel geklickt haben. Zum Beispiel haben wir für das Hinzufügen einer Figur das Katzen-Icon angeklickt oder wir haben ein neues Kostüm hinzugefügt, indem wir den Kostüm-Reiter ausgewählt haben und ein neues Bild hochgeladen haben. In Java bereiten wir unser Projekt vor, indem wir einen Konstruktor schreiben. Wir schreiben Text (Quelltext), welcher unser Geklicke ersetzt, da wir keine Benutzeroberfläche mehr haben.
- In Scratch hat jede Figur und die Bühne eine Menge von vordefinierte n Blöcken, welche wir benutzen konnten. In Java haben wir normalerweise nichts vordefiniertes und müssen alles selbst implementieren. Aber mit der Vererbung können wir auch vordefinierte Methoden (Blöcke) benutzen.
- In Scratch haben wir unseren Figuren Namen gegeben, aber wir haben die Namen gar nicht so viel benutzt. In Java sind Namen viel wichtiger, da wir nicht mit einer Figur oder der Bühne weiterarbeiten können, wenn wir keinen Namen vergeben hanbe. `Sprite cat = new Sprite();`
- In Scratch haben wir eine Figur der Bühne hinzugefügt, indem wir eine erstellt haben. In Java müssen wir explizit sein und schreiben, dass wir eine bestimmte Figur der Bühne hinzufügen wollen. `myStage.add(mySprite);`
- In Scratch haben wir oft den warte-Block zum Pausieren der Ausführung eines Skripts von einer Figur benutzt. In Java können wir nicht mehr so einfach ein Skript pausieren. Außer wir wollen das gesamte Programm pausieren (`myStage.wait(100)`). Wenn wir so etwas ähnlich mit Scratch for Java erreichen wollen, dann müssen wir die Timer-Klasse verwenden.
- In Scratch haben wir Nachrichten zu und von Figure geschickt, indem wir einen Block benutzt haben. In Java definieren wir eigene Methoden und rufen diese dann auf.

```java
import org.openpatch.scratch.*;

class Zebra extends Sprite {
    public Zebra() {
        this.addCostume("walk_1", "assets/walk_1.png");
        this.setOnEdgeBounce(true);
    }

    public void sayHello() {
        this.display("Hello");
    }
}

public class MyStage extends Stage {
    public MyStage() {
        Zebra aZebra = new Zebra();
        this.add(aZebra);
        aZebra.sayHello();
    }
}
```

## Erweiterte Scratch Ansatz

Der erweiterte Scratch Ansatz basiert auf dem objektorientierten Ansatz, aber führt weitere Konzepte und Klasse ein, welche sich vom Scratch-Modell unterscheiden. Dieser Ansatz ist so implementiert, dass du mit dem objektorientierten starten kannst und zu einem späteren Zeitpunkt zum erweiterten Scratch Ansatz wechseln kannst, wenn du die Limits erreicht hast.

Zum Beispiel gibt es in Scratch for Java die Window-Klasse. Welches nur einmalig instanziiert werden kann. Dieses Objekt kann mehrere Bühnen enthalten und zwischen ihnen wechseln. Du kannst auch die Größe des Fensters verändern.

```java
import org.openpatch.scratch.*;

public class MyProject extends Window {
    public MyProject() {
        super(800, 600);
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();

        this.setStage(stage1);
        // wechseln zu einer anderen Stage
        this.setStage(stage2);
    }
}
```

Eine andere Erweiterung ist die Klasse AnimatedSprite, welches es sehr einfach macht Animation zu verwenden. Die Klasse kann sehr früh verwendet werden, denn sie beseitig den Nachteil, dass Skripte nicht pausiert werden können.

```java
import org.openpatch.scratch.extensions.animation.*;

public Zebra extends AnimatedSprite {
    public Zebra() {
        this.addAnimation("walk", "walk_%d.png", 4);
    }

    public void run() {
        this.playAnimation("walk");
    }
}
```
