---
name: Donut IO
lang: en
---

# Donut IO

This example show the switch of stages and the interaction of multiple sprites.

![donut io example](/assets/donut-io.gif)

## Run it here

Click the stage and press space to start. Your donut follows the mouse. Eat the
smaller donuts and stay away from the bigger ones; 0 and 1 zoom out and in, R
resets the zoom.

<!-- demo: donutIO -->
:::onlineide{height="560px" libraries="scratch" speed="-1"}

@file dest="assets/donut.png" src="/examples/donut-io/assets/donut.png"
@file dest="assets/grid.png" src="/examples/donut-io/assets/grid.png"

```java Game.java

void main() {
  Text.useFontSizes(32, 48);
  new Game();
}

class Game extends Window {

  public static int LEVEL = 0;

  public Game() {
    super(800, 600, "assets");
    this.setStage(new StartStage());
  }
}

class StartStage extends Stage {
  public StartStage() {
    var bg = new Background();
    bg.setTransparency(50);
    this.add(bg);

    var text = new Text();
    text.setTextSize(48);
    text.setTextColor(200, 100, 100);
    text.showText("Donut.io");
    text.setPosition(0, 100);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.setWidth(700);
    text.showText("Each level more donuts will hunt you! Only the biggest will survive.");
    text.setPosition(0, 60);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.showText("Press Space to start.");
    text.setPosition(0, -20);
    this.add(text);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      Game.LEVEL = 0;
      Window.getInstance().transitionToStage(new WorldStage(), 500);
    }
  }
}

class WorldStage extends Stage {

  public static Vector2 CAM = new Vector2(0, 0);
  public boolean manualZoom;
  public double zoomInc = 0.1;
  public double targetZoom;

  public PlayerDonut player;

  public WorldStage() {
    this.add(new Background());

    manualZoom = false;

    player = new PlayerDonut();
    this.add(player);

    for (int i = 0; i < 5 * (Game.LEVEL + 1); i++) {
      var m = new FollowDonut(player);
      m.setStrength(Random.randomInt(10, 20 * (Game.LEVEL + 1)));
      this.add(m);
      do {
        var x = Random.random(-2000, 2000);
        var y = Random.random(-3000, 3000);
        var v = new Vector2(x, y);
        m.setPosition(v);
      } while (m.isTouchingSprite(player));
    }
  }

  public void run() {
    if (this.getTimer().everyMillis(1000)) {
      var food = new Donut();
      food.setStrength(Random.randomInt(2, 5));
      this.add(food);
      do {
        var x = Random.random(-400, 400);
        var y = Random.random(-300, 300);
        var v = new Vector2(x, y);
        // only spawn food around the player
        v = v.add(CAM);

        food.setPosition(v);
      } while (food.isTouchingSprite(player));
    }

    if (this.count(FollowDonut.class) == 0) {
      Window.getInstance().transitionToStage(new WinStage(), 500);
      return;
    }

    if (this.count(PlayerDonut.class) == 0) {
      Window.getInstance().transitionToStage(new GameOverStage(), 500);
      return;
    }

    if (this.isKeyPressed(KeyCode.DIGIT_1)) {
      this.getCamera().changeZoom(1);
      manualZoom = true;
    }
    if (this.isKeyPressed(KeyCode.DIGIT_0)) {
      this.getCamera().changeZoom(-1);
      manualZoom = true;
    }
    if (this.isKeyPressed(KeyCode.R)) {
      this.getCamera().resetZoom();
      manualZoom = false;
    }

    targetZoom = 10 / player.getStrength() * 100;

    if (!manualZoom && this.getCamera().getZoom() > targetZoom) {
      this.getCamera().changeZoom(-zoomInc);
    }
    this.getCamera().setPosition(player.getPosition());
  }
}

class WinStage extends Stage {
  public WinStage() {
    var bg = new Background();
    bg.setTransparency(50);
    this.add(bg);

    var text = new Text();
    text.setTextSize(48);
    text.setTextColor(200, 100, 100);
    text.showText("Level complete!");
    text.setPosition(0, 0);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.showText("Press Space for the next level!");
    text.setPosition(0, -40);
    this.add(text);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      Game.LEVEL += 1;
      Window.getInstance().transitionToStage(new WorldStage(), 500);
    }
  }
}

class GameOverStage extends Stage {
  public GameOverStage() {
    var bg = new Background();
    bg.setTransparency(50);
    this.add(bg);

    var text = new Text();
    text.setTextSize(48);
    text.setTextColor(200, 100, 100);
    text.showText("Level " + Game.LEVEL + " is too hard for you!");
    text.setPosition(0, 0);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.showText("Press Space to start again!");
    text.setPosition(0, -40);
    this.add(text);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    System.out.println(keyCode);
    if (keyCode == KeyCode.SPACE) {
      Game.LEVEL = 0;
      Window.getInstance().transitionToStage(new WorldStage(), 500);
    }
  }
}

class Background extends Sprite {

  public Background() {
    this.addCostume("grid", "assets/grid.png");
  }

  public void run() {
    this.getPen().eraseAll();
    this.setX(this.getStage().getCamera().getX() - this.getStage().getCamera().getX() % 40);
    this.setY(this.getStage().getCamera().getY() - this.getStage().getCamera().getY() % 40);
  }
}

class Donut extends Sprite {

  private int strength;
  protected double speed = 1;

  public Donut() {
    this(0, 0, 2);
  }

  public Donut(double x, double y, int strength) {
    this.addCostume("donut", "assets/donut.png");
    this.setHitbox(new Ellipse(0, 0, 512, 480));
    this.setX(x);
    this.setY(y);

    this.setStrength(strength);

    this.setTint(Random.randomInt(240), Random.randomInt(240), Random.randomInt(240));
  }

  public void setStrength(int strength) {
    this.strength = strength;
    this.setSize(strength);
  }

  public double getStrength() {
    return this.strength;
  }

  public void run() {
    var touchingDonut = this.getTouchingSprite(Donut.class);
    debug("touchingDonut: " + getX());
    if (touchingDonut != null && this.strength >= touchingDonut.strength) {
      this.setStrength(this.strength + touchingDonut.strength);
      touchingDonut.remove();
    }
  }
}

class PlayerDonut extends Donut {

  public PlayerDonut() {
    super(0, 0, 10);
  }

  public void run() {
    var v = this.getMouse();
    this.setPosition(
        v.sub(this.getPosition()).unitVector().multiply(this.speed).add(this.getPosition()));

    super.run();
  }
}

class FollowDonut extends Donut {

  private PlayerDonut player;

  public FollowDonut(PlayerDonut player) {
    this.player = player;
  }

  public void run() {
    var v = player.getPosition().sub(this.getPosition()).unitVector();
    this.setPosition(this.getPosition().add(v).multiply(this.speed));
    super.run();
  }
}
```

:::

To run it on your own computer, copy the folder `assets` of the
[source code](https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/donutIO) next to the program.

## Source Code

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/donutIO
