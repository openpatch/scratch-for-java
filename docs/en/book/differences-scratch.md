---
name: Differences to Scratch
index: 2
---

# Differences to Scratch

Probably the most noticeable differences to Scratch are the following:

- The coordinated start in the top left hand side corner and **not** in the center.
- There is not wait-block on a Sprite-level, you need to use [Timers](/reference/sprite/sensing/getTimer).

If you want to achieve something like this inside a Sprite:

:::scratchblock
when green flag clicked
forever
next costume
wait (1) seconds
:::

You can use a timer.

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


- There is no Sprite library, you have to search the internet.
- There is no Sound library, you have to search the internet.
- There are not build-in editors, you have to use external tools like [GIMP](https://www.gimp.org/), [Inkscape](https://inkscape.org/), [Audacity](https://www.audacityteam.org/) and so on.
- If you want to share your project with others, you have to used external sharing platforms like Nextcloud, iCloud, Dropbox or better a code sharing platform like GitHub.
- You **can not** use a forever loop. This will halt you program.

If you want to achieve something like this inside a Sprite:

:::scratchblock
when green flag clicked
forever
move (10) steps
:::

You can use the run-method of the Sprite-class.

```java
public class Cat extends Sprite {
    // executes 60-times a second, if the sprite is added to a stage.
    public void run() {
        this.move(10);
    }
}
```

- If you want to have a variable for all sprites, you have to use a static attribute. Static attributes are available across all objects.

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

- There are no send, broadcast or receive message methods, you need to make use of custom-methods and object-references.

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