---
name: Robot
---

# Robot

An example with a stage and a sprite class of its own. In the source code each
class has a file of its own; here they share one.

![robot example](/assets/robot.gif)

## Run it here

The robot bounces around the stage. Debug mode is on, so you see its hitbox and
its position while it moves.

<!-- demo: robot -->
:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="sprites/robot.png" src="/examples/robot/sprites/robot.png"

```java RobotStage.java

void main() {
  new RobotStage();
}

class RobotStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private RobotSprite robotSprite;
  // scratch4j:end fields

  public RobotStage() {
    super(800, 600);
    this.setDebug(true);
    // scratch4j:begin setup (managed by the stage designer)
    robotSprite = new RobotSprite();
    this.add(robotSprite);
    // scratch4j:end setup
  }
}

class RobotSprite extends Sprite {

  public RobotSprite() {
    // scratch4j:begin setup (managed by the stage designer)
    this.addCostume("robot", "sprites/robot.png");
    // scratch4j:end setup
    this.setSize(20);
    this.changeY(20);
    this.setDirection(45);
  }

  public void run() {
    this.move(2);
    this.ifOnEdgeBounce();
  }
}
```

:::

To run it on your own computer, copy the folder `sprites` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/robot) next to the program.

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/robot
