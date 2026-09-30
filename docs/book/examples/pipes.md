---
name: Pipes
---

# Pipes

An example with heavy use of the Pen.

![pipes example](/assets/pipes.gif)

## Run it here

The pens wander over a chalk board and split into new pens as they go. Click the
stage, then press space for a new colour and H to show or hide the pens. The
browser only plays the music after that first click.

<!-- demo: pipes -->
:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="backdrops/chalk_board.jpg" src="/examples/pipes/backdrops/chalk_board.jpg"
@file dest="sounds/bensound-enigmatic.ogg" src="/examples/pipes/sounds/bensound-enigmatic.ogg"
@file dest="sprites/pen.png" src="/examples/pipes/sprites/pen.png"

```java Pipes.java

void main() {
  new Pipes();
}

class Pipes extends Stage {
  public Pipes() {
    super(1280, 800);
    this.addBackdrop("chalkBoard", "backdrops/chalk_board.jpg");
    this.setTint(60);
    this.add(new PenSprite());
    this.addSound("bg", "sounds/bensound-enigmatic.ogg");
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      PenSprite.setColor(Random.random(255));
    }
  }

  public void run() {
    this.playSound("bg");
    var pens = this.find(PenSprite.class);
    for (var pen : pens) {
      if (Random.random() < 0.05 && pens.size() < 15) {
        this.add(new PenSprite((PenSprite) pen));
      }
      if (Random.random() < 0.01 && pens.size() > 1) {
        this.remove(pen);
      }
    }
  }
}

class PenSprite extends Sprite {

  private boolean finished = false;
  private static double color;

  public PenSprite() {
    super("pen", "sprites/pen.png");
    this.getPen().down();
    this.getPen().setSize(2);
    color = Random.random(255);
    this.getPen().setColor(color);
    this.hide();
  }

  public static void setColor(double color) {
    PenSprite.color = color;
  }

  // when I start as a clone
  public PenSprite(PenSprite pen) {
    super(pen);
    this.setDirection(pen.getDirection() + 90);
    this.getPen().setColor(color);
    if (Math.random() < 0.05) {
      color += Math.random() * 10;
    }
  }

  public void setFinished() {
    this.finished = true;
  }

  public void whenKeyPressed(KeyCode keyCode) {

    if (keyCode != KeyCode.H) {
      return;
    }

    if (this.isVisible()) {
      this.hide();
    } else {
      this.show();
    }
  }

  public void run() {
    if (!this.finished) {
      this.move(1);
      this.ifOnEdgeBounce();
      if (Math.random() < 0.05) {
        int newRotation = Random.randomInt(4) * 90;
        this.setDirection(newRotation);
      }
    }
  }
}
```

:::

To run it on your own computer, put
[pen.png](/examples/pipes/sprites/pen.png) in a folder `sprites`,
[chalk_board.jpg](/examples/pipes/backdrops/chalk_board.jpg) in a folder
`backdrops` and
[bensound-enigmatic.ogg](/examples/pipes/sounds/bensound-enigmatic.ogg) in a
folder `sounds` next to the program.

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/pipes
