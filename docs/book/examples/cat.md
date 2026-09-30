---
name: Cat
---

# Cat

An example with a simple one file setup.

![cat example](/assets/cat.gif)

## Run it here

The cat walks and bounces off the edges. Press space and it leaves a stamp of
itself behind.

:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="sprites/cat.png" src="/examples/cat/sprites/cat.png"

```java CatSketch.java

void main() {
  new CatSketch();
}

class CatSketch extends Stage {

  public CatSketch() {
    super(800, 600);
    Sprite myCat = new CatSprite();
    this.add(myCat);
  }
}

class CatSprite extends Sprite {

  CatSprite() {
    this.addCostume("cat", "sprites/cat.png");
    this.setDirection(0);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      this.stamp();
    }
  }

  public void run() {
    this.move(2);
    this.ifOnEdgeBounce();
  }
}
```

:::

To run it on your own computer, put [cat.png](/examples/cat/sprites/cat.png) in
a folder `sprites` next to the program.

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/cat
- Scratch: https://scratch.mit.edu/projects/339257357/
