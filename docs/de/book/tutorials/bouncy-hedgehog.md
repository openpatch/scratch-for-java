---
name: Hüpfende Igel
index: 2
lang: de
---

# Hüpfende Igel

Spike, der hüpfende Igel, liebt es auf seinem Trampolin zu springen, aber er ist ein bisschen ungeschickt. Kannst du das Trampolin bewegen, sodass er nicht mehr auf den Boden fällt?

In diesem Kapitel wirst du dein erste Spiel programmieren. In diesem Spiel wirst du die Pfeiltasten benutzen können, um ein Trampolin von links nach rechts zu bewegen. Dein Ziel ist es mit dem Trampolin ein hüpfendes Ziel zu fangen. Dieses Projekt zeigt dir wie du neue Figuren (Sprites) und Hintergründe (Backgrounds) hinzufügst und wie du bedingten Anweisungen das Verhalten in deinem Projekt kontrollierst. Diese Fähigkeiten werden dir auch bei den folgenden Projekten helfen.

## Schritt 1: Projektvorlage herunterladen

Die Bilder zusammen mit einem Grundgerüst für dein BlueJ-Projekt kannst du hier herunterladen.

::archive[Projekt: Bouncy Hedgehog]{name="bouncy-hedgehog"}

## Schritt 2: Das Projekt ausführen

Lass uns anschauen, wie das Projekt im Ausgangszustand aussieht. Klicke mit der rechten Maustaste auf die Klasse `BouncyHedgehogStage` und erstelle ein neues Objekt, indem du auf `new BouncyHedgehogStage()` klickst.

Ein Fenster sollte sich öffnen, in dem du den Spielplatz siehst.

## Schritt 3: Das Trampolin der Bühne hinzufügen

Öffne die Klasse TrampolineSprite in BlueJ durch einen Doppelklick. Die Klasse ist schon für dich vorbereitet. Wir gehen sie nun Zeile für Zeile durch, damit du verstehst, was jede Zeile bedeutet. Keine Sorge, du musst noch nicht alles sofort nachvollziehen können – das Verständnis wächst mit der Zeit. 

```java
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
    }
}
```

- Zeile 1: Hier wird eine Klasse importiert. Das bedeutet, dass wir den Quelltext von jemand anderes nutzen, um uns die Arbeit zu erleichtern. Wir verwenden die Klasse Sprite von Scratch4j, damit wir uns nicht darüm kümmern müssen wie z.B. Bilder aus dem Bildschirm angezeigt werden können.

- Zeile 3: Hier definieren wir eine eigene Klasse Names `TrampolineSprite` und diese Klasse soll die Klasse `Sprite` erweitern. Also die Funktionen der bereits implementierten Klasse `Sprite` nutzen.

- Zeile 4-6: Hier definieren wir einen Konstruktor. Diese wird ausgeführt, sobald wir ein Trampolin erzeugen - dazu später mehr. Hier definieren wir, dass wir ein Kostüm hinzufügen möchten, welches wir `trampoline` nennen und die Datei `trampoline.png` verwenden soll.

Jetzt fügen wir das Trampolin unserer Bühne hinzu. Klicke dazu die Klasse `BouncyHedgehogStage` doppelt an. Hier siehst du ähnliche Zeilen Quelltext. 

```java
import org.openpatch.scratch.Stage;

public class BouncyHedgehogStage extends Stage {
    
    public BouncyHedgehogStage() {
        this.addBackdrop("playground", "playground.jpg");
    }
}
```

Diesesmal erbt die Klasse jedoch von Stage (Zeile 3), da es sich hier um eine Bühne handeln soll. Daher wird im Konstruktor auch kein Kostüm hinzugefügt, sondern ein Hintergrund (siehe Zeile 6).

Damit wird nun auch das Trampolin sehen können, ergänzen wir im Konstruktor eine Zeile Quelltext.

```java
import org.openpatch.scratch.Stage;

public class BouncyHedgehogStage extends Stage {
    
    public BouncyHedgehogStage() {
        this.addBackdrop("playground", "playground.jpg");
        this.add(new TrampolineSprite());
    }
}
```

Die neue Zeile ruft zuerst den Konstruktor der Klasse `TrampolineSprite` auf und fügt das neuerstelle Objekt dann der Bühne mit dem Methodenaufruf `this.add(new TrampolineSprite())` hinzu.

Führe das Projekt jetzt erneut aus. Das Trampolin sollte jetzt in der Mitte der Bühne zu sehen sein.

## Schritt 4: Den Igel hinzufügen

Schaffst du es den Igel hinzufügen? Orientiere dich am vorherigen Schritte und benutze diesesmal die Klasse `HedgehogSprite`.

## Schritt 5: Das Trampolin bewegen

Bis jetzt sind beide Sprite in der Mitte der Bühne platziert. Nun werden wir zuerst das Trampolin an eine gute Ausgangsposition bringen. Anschließend werden wir es so programmieren, sodass wir es mit den Pfeiltasten steuern können.

Um das Trampolin zu platzieren, ergänzen wir den Konstruktor der Klasse `TrampolineSprite` um eine weitere Zeile.

```java
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
        this.setPosition(0, -120);
    }
}
```

Mit dieser Zeile platzieren wir das Trampolin in x-Richtung (links-rechts) auf 0, das heißt in die Mitte, und in y-Richtung (oben-unten) auf -120, das heißt 120 Pixel von der Mitte aus nach unten.

Schaue dir das Ergebnis an, indem du das Projekt ausführst. Mache dazu einen Rechtsklick auf die Klasse `BouncyHedgehogStage` und wähle `new BouncyHedgehogStage()` aus.

## Schritt 6: Den Igel platzieren

Versuche den Igel auf die Position (x: -180, y: 140) zu platzieren.

## Schritt 7: Das Trampolin bewegen

Wir möchten das Trampolin mit den Pfeiltasten bewegen. Dazu ergänzen wir die Klasse `TrampolinSprite` wie folgt:

```java
import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Sprite;

public class TrampolineSprite extends Sprite {
    public TrampolineSprite() {
        this.addCostume("trampoline", "trampoline.png");
        this.setPosition(0, -120);
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyCode.VK_LEFT) {
            this.changeX(-10);
        } else if (keyCode == KeyCode.VK_RIGHT) {
            this.changeX(10);
        }
    }
}
```

Wir haben der Klasse eine neue Methode `whenKeyPressed` hinzufügt. Methoden erlauben uns den Objekten einer Klasse bestimmte Verhalten zu implementieren. In diesem Fall ist `whenKeyPressed` eine besondere - von Scratch4j vorgegebene - Methode. Sie wird immer aufgerufen, wenn eine Taste auf der Tastatur gedrückt wird. In der Variablen `keyCode` steckt dann der Zahlencode dieser Taste.

Wenn du z.B. die Taste `A` drückst, dann steckt in der Variablen `keyCode` der Wert `65`. Damit wir uns nicht alle Werte der einzelnen Tasten merken müssen, gibt es die Klasse `KeyCode`. Zum Beispiel würde hinter `KeyCode.VK_A` der Wert `65` stehen. So wird der Quelltext auch nachvollziehbarer. Da das wieder eine Klasse von Scratch4j ist, müssen wird diese auch importieren (siehe erste Zeile).

Jetzt können wir definieren was passieren soll, wenn eine Taste gedrückt wird.

Wenn die gedrückte Taste die linke Pfeiltaste ist, dann änderen wir die x-Position um -10 Pixel. Wir bewegen das Trampolin also nach links.
Andernfalss, wenn die gedrückte Taste die rechte Pfeiltaste ist, dann ändern wir die x-Position um 10 Pixel. Wir bewegen das Trampolin also nach rechts.

So, jetzt ist es wieder Zeit das Projekt auszuprobieren. Die Schritte dazu kennst du ja schon.

## Schritt 8: Den Igel hüpfen lassen

Der Igel soll automatisch herunterfallen und sich wieder nach oben bewegen, wenn er das Trampolin berührt. Dies erreichen wir mit folgendem Code:

```java
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.extensions.math.Random;

public class HedgehogSprite extends Sprite {

    public HedgehogSprite() {
        this.addCostume("hedgehog", "hedgehog.png");

        this.pointInDirection(15);
        this.setPosition(-180, 140);
    }

    public void run() {
        if (this.getY() > -120) {
            this.move(1);
            this.ifOnEdgeBounce();

            if (this.isTouchingSprite(TrampolineSprite.class)) {
                this.pointInDirection(Random.random(-45, 45));
            }
        } else {
            this.say("Ouch!", 2000);
        }
    }
}
```

Überlege anhand der Methodenbezeichner (isTouchingSprite, pointInDirection), was passiert, wenn das Projekt ausgeführt wird. Führe das Projekt aus und überprüfe deine Vermutungen.

## Abschluss

Herzlichen Glückwunsch, du hast dein erstes Spiel mit Scratch4j programmiert! Auch wenn dir noch nicht alles klar ist, bleib dran – Übung macht den Meister.

Experimentiere ruhig weiter: Mache den Igel schneller, setze das Trampolin mit der Leertaste wieder in die Mitte oder tausche die Grafiken aus. Wenn du mutiger wirst, probiere die Methode [setTint](/reference/sprite/looks/setTint) aus.

Weiterhin viel Spaß :smiley:!

::archive[Projekt: Bouncy Hedgehog 100%]{name="bouncy-hedgehog-100"}
