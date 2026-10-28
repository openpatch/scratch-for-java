---
name: Tiled
lang: en
---

# Tiled

This example show the handling of the tiled extension.

![tiled](/assets/tiled.gif)

## Run it here

<!-- demo: tiled -->
:::onlineide{height="800px" libraries="scratch"}

@file dest="Level1.tmx" src="/examples/tiled/Level1.tmx"
@file dest="Level2.tmx" src="/examples/tiled/Level2.tmx"
@file dest="assets/Bamboo.png" src="/examples/tiled/assets/Bamboo.png"
@file dest="assets/Fireball.png" src="/examples/tiled/assets/Fireball.png"
@file dest="assets/Retro Gaming.ttf" src="/examples/tiled/assets/Retro Gaming.ttf"
@file dest="assets/ScrollFire.png" src="/examples/tiled/assets/ScrollFire.png"
@file dest="assets/ScrollWater.png" src="/examples/tiled/assets/ScrollWater.png"
@file dest="assets/Skeleton.png" src="/examples/tiled/assets/Skeleton.png"
@file dest="assets/TilesetField.png" src="/examples/tiled/assets/TilesetField.png"
@file dest="assets/TilesetFloor.png" src="/examples/tiled/assets/TilesetFloor.png"
@file dest="assets/TilesetWater.png" src="/examples/tiled/assets/TilesetWater.png"

```java Tiled.java

public class Tiled extends Window {

  public Tiled() {
    super(640, 360, "assets");

    I18n.setup();

    GameState.load();
    I18n.select(GameState.get().locale);

    this.setStage(new Level(GameState.get().map, null));

    // begin project settings
    // end project settings
  }

  public static void main(String[] args) {
    // These global settings must be set before starting the game.
    Text.useFont("assets/Retro Gaming.ttf");

    // Start the Game
    // begin project settings
    Window.usePixelArt();
    Window.useFullScreen();
    // end project settings
    new Tiled();
  }
}
```

```java Level.java

public class Level extends Stage {

  // begin stage designer
  private UIItem inventory;
  // end stage designer

  private TiledMap map;
  private Player player;
  public Level(String mapFile, Player player) {
    map = new TiledMap("" + mapFile + ".tmx", this);
    // Every object whose type is one of these classes becomes that sprite:
    // walls, water, warps, items and enemies.
    for (Sprite sprite : map.spawn("Objects", Wall.class, Water.class, Warp.class, Item.class,
        Bamboo.class)) {
      // an item that was picked up stays picked up
      if (sprite instanceof Item item && GameState.hasItem(item.getName())) {
        this.remove(item);
      }
    }

    // The player comes along through a warp. A new game starts it at the
    // Player object of the Start layer, a saved game where it was saved.
    if (player == null) {
      if (GameState.get().playerX == Double.MAX_VALUE) {
        MapObject start = map.getObjectsFromLayer("Start")[0];
        GameState.get().playerX = start.x;
        GameState.get().playerY = start.y;
      }
      player = new Player(GameState.get().playerX, GameState.get().playerY);
    }
    this.add(player);
    // run() starts after the transition: until then the camera has to be on the player already
    this.getCamera().setPosition(player.getX(), player.getY());
    this.setColor(0, 0, 0);
    // the map's layers are drawn every frame from now on, under the sprites
    this.map.showLayer("Floor", Layer.BACKGROUND);
    this.map.showLayer("FloorObjects", Layer.BACKGROUND);
    this.map.showLayer("Walls", Layer.BACKGROUND);

    GameState.get().map = mapFile;
    // begin stage designer
    inventory = new UIItem();
    this.add(inventory);
    // end stage designer
    this.player = player;

  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.F1) {
      Window.getInstance().setDebug(!Window.getInstance().isDebug());
    } else if (keyCode == KeyCode.F5) {
      GameState.save();
      this.display("Game saved", 2);
    }
  }

  public void run() {
    this.getCamera().setPosition(player.getPosition());

    if (this.isKeyPressed(KeyCode.DIGIT_1)) {
      this.getCamera().changeZoom(1);
    }
    if (this.isKeyPressed(KeyCode.DIGIT_0)) {
      this.getCamera().changeZoom(-1);
    }

    var items = GameState.get().items;
    for (int i = 0; i < items.length; i++) {
      inventory.setX(-this.getWidth() / 2 + 30);
      inventory.setY(this.getHeight() / 2 - 30 - i * 36);
      inventory.switchCostume(items[i]);
      inventory.stamp(Layer.UI);
    }
  }

  public Player getPlayer() {
    return player;
  }
}
```

```java I18n.java

public class I18n {
  private static String[] allowedLocales = { "en", "de" };
  private static String locale = allowedLocales[0];
  private static HashMap<String, HashMap<String, String>> messages;

  public static void setup() {
    messages = new HashMap<>();

    for (var allowedLocale : allowedLocales) {
      messages.put(allowedLocale, new HashMap<>());
    }

    // a message may name its arguments with {0}, {1}, ... in the text
    var en = messages.get("en");
    en.put("item-found", "You have found an item: {0}!");
    en.put("scroll-water", "Scroll of Water");
    en.put("scroll-fire", "Scroll of Fire");
    en.put("tutorial-item-pickup", "Press E to pick up an item!");
    en.put("tutorial-scroll-fire", "Press G to throw a fire ball!");
    en.put("need-scroll-water", "I probably need the water scroll. Maybe it is here somewhere!");

    var de = messages.get("de");
    de.put("item-found", "Du hast einen Gegenstand gefunden: {0}!");
    de.put("scroll-water", "Schriftrolle des Wassers");
    de.put("scroll-fire", "Schriftrolle des Feuers");
    de.put("tutorial-scroll-fire", "Drücke G, um einen Feuerball zu werfen!");
    de.put("tutorial-item-pickup", "Drücke E, um das Item aufzuheben!");
    de.put(
        "need-scroll-water",
        "Ich brauche Wahrscheinlich die Schriftrolle des Wassers. Vielleicht ist sie hier"
            + " irgendwo?");
  }

  public static void select(String locale) {
    for (var allowedLocale : allowedLocales) {
      if (allowedLocale.equals(locale)) {
        I18n.locale = locale;
        return;
      }
    }
  }

  public static String get(String key, String... args) {
    var message = messages.get(locale).get(key);
    if (message == null) {
      return "NOT_FOUND";
    }
    for (int i = 0; i < args.length; i++) {
      message = message.replace("{" + i + "}", args[i]);
    }
    return message;
  }
}
```

```java GameState.java

public class GameState {
  public String[] items = new String[0];
  public double playerX = Double.MAX_VALUE;
  public double playerY = Double.MAX_VALUE;
  public String map = "Level1";
  public String locale = "de";
  private static GameState instance;
  private static SaveData<GameState> saves = new SaveData<>("demos.tiled", "slot1", GameState.class);

  public static void save() {
    try {
      saves.save(get());
    } catch (IOException error) {
      // without a save the game keeps running, it just starts over next time
    }
  }

  public static void load() {
    try {
      instance = saves.loadOrDefault(() -> new GameState());
    } catch (IOException error) {
      instance = new GameState();
    }
  }

  public static GameState get() {
    if (instance == null) {
      instance = new GameState();
    }
    return instance;
  }

  public static void addItem(String name) {
    var items = get().items;
    var grown = new String[items.length + 1];
    for (int i = 0; i < items.length; i++) {
      grown[i] = items[i];
    }
    grown[items.length] = name;
    get().items = grown;
  }

  public static boolean hasItem(String name) {
    for (var item : get().items) {
      if (item.equals(name)) {
        return true;
      }
    }
    return false;
  }
}
```

```java Item.java

public class Item extends Sprite {
  private String name;

  // Constructor for displaying the inventory
  public Item() {
    // begin sprite editor
    this.addCostume("scroll-water", "assets/ScrollWater.png");
    this.addCostume("scroll-fire", "assets/ScrollFire.png");
    // end sprite editor
    this.hide();
  }

  // The map makes an item for each Item object; the object's name is the item
  public Item(MapObject object) {
    this();
    this.show();
    this.name = object.name;
    this.switchCostume(name);
  }

  public void collect() {
    GameState.addItem(name);
    // auto-save when an item is collected
    GameState.save();
    this.hide();
    this.getStage().display(I18n.get("item-found", I18n.get(name)), 2);

    if (name.equals("scroll-fire")) {
      this.getStage().display(I18n.get("tutorial-scroll-fire"), 2);
    }
  }

  public String getName() {
    return name;
  }
}
```

```java UIItem.java

/** An Item pinned to the user interface layer, used for the inventory. */
public class UIItem extends Item {
  public UIItem() {
    this.setLayer(Layer.UI);

    // begin sprite editor
    // end sprite editor
  }
}
```

```java Player.java

public class Player extends Sprite {

  private String state = "idle";
  private double speed = 2;
  private String dir = "down";

  public Player(double x, double y) {
    this.setX(x);
    this.setY(y);

    // begin sprite editor
    this.addAnimation("walk-down", "assets/Skeleton.png", 32, 32, 0, 4, 8, 12); // column 0
    this.addAnimation("walk-up", "assets/Skeleton.png", 32, 32, 1, 5, 9, 13); // column 1
    this.addAnimation("walk-left", "assets/Skeleton.png", 32, 32, 2, 6, 10, 14); // column 2
    this.addAnimation("walk-right", "assets/Skeleton.png", 32, 32, 3, 7, 11, 15); // column 3

    this.setHitbox(new Polygon(new double[] {1, 1, 30, 30}, new double[] {31, 10, 10, 31}));
    // end sprite editor
  }

  public boolean hasItem(String name) {
    return GameState.hasItem(name);
  }

  public void tryMove(double dx, double dy) {
    this.changeX(dx);
    this.changeY(dy);
    if (!hasItem("scroll-water") && this.isTouchingSprite(Water.class)) {
      this.think(I18n.get("need-scroll-water"), 5);
      this.changeX(-dx);
      this.changeY(-dy);
    }
    if (this.isTouchingSprite(Wall.class)) {
      this.changeX(-dx);
      this.changeY(-dy);
    }
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (hasItem("scroll-fire") && keyCode == KeyCode.G) {
      var f = new Fireball(this.getX(), this.getY(), dir);
      this.getStage().add(f);
    }
  }

  public void run() {
    state = "idle";

    var dx = 0.0;
    var dy = 0.0;

    if (this.isKeyPressed(KeyCode.W) || this.isKeyPressed(KeyCode.UP)) {
      dy += this.speed;
      state = "walk";
      dir = "up";
    }

    if (this.isKeyPressed(KeyCode.S) || this.isKeyPressed(KeyCode.DOWN)) {
      dy -= this.speed;
      state = "walk";
      dir = "down";
    }

    if (this.isKeyPressed(KeyCode.A) || this.isKeyPressed(KeyCode.LEFT)) {
      dx -= this.speed;
      state = "walk";
      dir = "left";
    }

    if (this.isKeyPressed(KeyCode.D) || this.isKeyPressed(KeyCode.RIGHT)) {
      dx += this.speed;
      state = "walk";
      dir = "right";
    }

    var item = this.getTouchingSprite(Item.class);
    if (item != null && this.isKeyPressed(KeyCode.E)) {
      item.collect();
    } else if (item != null) {
      this.getStage().display(I18n.get("tutorial-item-pickup"), 1);
    }

    if (state.equals("idle")) {
      this.resetAnimation();
    }

    this.playAnimation("walk-" + dir);

    GameState.get().playerX = this.getX();
    GameState.get().playerY = this.getY();

    this.tryMove(dx, 0);
    this.tryMove(0, dy);
  }
}
```

```java Wall.java

public class Wall extends Sprite {
  // The map puts it where its object is, and makes the object's area its hitbox.
  public Wall() {
    // begin sprite editor
    // end sprite editor
  }
}
```

```java Water.java

public class Water extends Sprite {
  // The map puts it where its object is, and makes the object's area its hitbox.
  public Water() {
    // begin sprite editor
    // end sprite editor
  }
}
```

```java Warp.java

public class Warp extends Sprite {
  private String toMap;
  private double toX;
  private double toY;

  // The map makes a warp for each Warp object; the object says where it leads.
  public Warp(MapObject object) {
    this.toMap = object.getProperty("to_map");
    this.toX = object.getPropertyInt("to_x");
    // Tiled counts y downwards, the stage upwards
    this.toY = -object.getPropertyInt("to_y");

    // begin sprite editor
    // end sprite editor
  }

  public void run() {
    var level = (Level) this.getStage();
    var player = level.getPlayer();
    if (this.isTouchingSprite(player)) {
      // Move the player and build the room only when the screen is black.
      // A request during another transition leaves this supplier untouched.
      Window.getInstance().transitionToStage(() -> {
        player.setX(toX);
        player.setY(toY);
        return new Level(toMap, player);
      }, 0.5);
    }
  }
}
```

```java Enemy.java

public class Enemy extends Sprite {

  protected String state = "idle";
  protected String dir = "down";

  public Enemy(double x, double y) {
    this.setX(x);
    this.setY(y);

    // begin sprite editor
    // end sprite editor
  }
}
```

```java Bamboo.java

public class Bamboo extends Enemy {

  protected double speed = 2;

  public Bamboo(double x, double y) {
    super(x, y);

    // begin sprite editor
    this.addAnimation("walk-down", "assets/Bamboo.png", 32, 32, 0, 4, 8, 12); // column 0
    this.addAnimation("walk-up", "assets/Bamboo.png", 32, 32, 1, 5, 9, 13); // column 1
    this.addAnimation("walk-left", "assets/Bamboo.png", 32, 32, 2, 6, 10, 14); // column 2
    this.addAnimation("walk-right", "assets/Bamboo.png", 32, 32, 3, 7, 11, 15); // column 3
    // end sprite editor
  }

  public void run() {
    state = "walk";

    this.changeX(this.speed);

    if (this.speed > 0) {
      dir = "right";
    } else {
      dir = "left";
    }
    this.changeY(0);
    if (this.isTouchingSprite(Wall.class)) {
      state = "idle";
      this.changeX(-this.speed);
      this.speed *= -1;
      this.changeY(0);
    }

    if (this.isTouchingSprite(Fireball.class)) {
      this.remove();
    }

    if (state.equals("idle")) {
      this.setAnimationFrame(0);
    }

    this.playAnimation("walk-" + dir);
  }
}
```

```java Fireball.java

public class Fireball extends Sprite {
  private String dir;
  private double speed = 3;

  public Fireball(double x, double y, String dir) {
    this.setX(x);
    this.setY(y);
    this.dir = dir;

    // begin sprite editor
    this.addAnimation("default", "assets/Fireball.png", 32, 32, 0, 1, 2, 3);
    // end sprite editor
  }

  public void run() {
    if (this.getTimer().after(1)) {
      this.remove();
    }

    if (dir == "left") {
      this.changeX(-speed);
      this.setDirection(0);
    } else if (dir == "right") {
      this.changeX(speed);
      this.setDirection(180);
    } else if (dir == "up") {
      this.setDirection(90);
      this.changeY(speed);
    } else if (dir == "down") {
      this.setDirection(270);
      this.changeY(-speed);
    }

    this.playAnimation("default");
  }
}
```

:::

## Source Code:

- Java: https://github.com/openpatch/scratch-for-java/tree/main/src/examples/java/demos/tiled
