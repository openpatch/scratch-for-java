---
name: Unterschiede zu Scratch
index: 2
lang: de
---

# Unterschiede zu Scratch

Die wahrscheinlich auffallensten Unterschiede sind die folgenden:

- Das Koordinatensystem startet in der oberen linken Ecke und **nicht** im Zentrum
- Es existiert kein Warte-Block, du musst [Timer](/reference/sprite/sensing/getTimer) verwenden.

Wenn du etwas ähnlich wie folgende erreichen möchtest:

:::scratchblock
when green flag clicked
forever
next costume
wait (1) seconds
:::

Kannst du in Scratch for Java Timer verwenden.

```java
public class Cat extends Sprite {
    // executes 60-times a second, if the sprite is added to a stage.
    public void run() {
        if (this.getTimer().everyMillis(1000)) {
            this.nextCostume();
        }
    }
}
```


- Es gibt keine Figur-Bibliothek, dafür musst du im Internet suchen.
- Es gibt keine Klang-Bibliothek, dafür musst du im Internet suchen.
- Es gibt keine eigengebauten Editoren für Grafiken und Audiodateien.
- Wenn du dein Projekt mit anderen teilen möchtest, musst du eine externe Austauschplattform verwenden wie Nextcloud, iCloud, Dropbox oder besser Plattformen zum Austausch von Quelltexten wie GitHub oder Edugit.
- Du kannst **keine** Endlosschleife verweden. Das wurde dein Programm anhalten.

Wenn du etwas erreichen möchtest wie das foldende

:::scratchblock
when green flag clicked
forever
move (10) steps
:::

dann kannst du die run-Methode der Sprite-Klasse verwenden.

```java
public class Cat extends Sprite {
    // executes 60-times a second, if the sprite is added to a stage.
    public void run() {
        this.move(10);
    }
}
```

- Wenn du eine Variable für alle Figuren verwenden möchtest, dann kannst du statische Attribute verwenden.

```java
public class Cat extends Sprite {
    public static int hitCounter = 1;
}

public class MyProgram {
    public MyProgram() {
        Cat.hitCounter += 1;
    }
}
```

- Es existieren keine sende und empfange Methoden. Du musst eigene Methode schreiben und Objektreferenzen verwenden.

```java
public class Sender extends Sprite {
    private Receiver receiver;

    public Sender(Receiver aReceiver) {
        receiver = aReceiver;
    }

    public whenClicked() {
        aReceiver.receive("Hello");
    }

}

public class Receiver extends Sprite {
    public void receive(String text) {
        this.say(text);
    }
}

public class MyProgram {
    Stage myStage = new Stage();
    Receiver myReceiver = new Receiver();
    myStage.add(myReceiver);
    Sende mySender = new Sender(myReceiver);
    myStage.add(mySender);
}
```