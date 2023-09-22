import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.TiledMap;

public class World extends Stage {

  private TiledMap map;
  public static Player PLAYER;
  private Item inventory;

  public World() {
    GameState.load();
    I18n.select(GameState.get().locale);
    this.loadMap(GameState.get().map);
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_F1) {
      this.setDebug(!this.isDebug());
    } else if (keyCode == KeyCode.VK_F5) {
      GameState.save();
      this.display("Game saved", 2000);
    }
  }

  public void loadMap(String mapFile) {
    this.removeAll();
    this.eraseAll();
    map = new TiledMap(mapFile + ".tmx", this);
    for (var object : map.getObjectsFromLayer("Objects")) {
      // Spawn Points should only have an effect, if there was no PLAYER object. Meaning the first
      // time a map is loading.
      if ("spawn-point".equals(object.type) && PLAYER == null) {
        var camX = GameState.get().camX;
        // check camX and camY are not saved in GameState
        if (camX == Double.MAX_VALUE) {
          GameState.get().camX = object.x;
          GameState.get().camY = object.y;
        }
      } else if ("warp".equals(object.type)) {
        var x = object.getPropertyInt("to_x");
        var y = object.getPropertyInt("to_y");
        var to = object.getProperty("to_map");
        var warp = new Warp(this, object.x, object.y, to, x, -y);
        warp.setHitbox(object.getShape());
        this.add(warp);
      } else if ("wall".equals(object.type)) {
        var wall = new Wall(object.x, object.y);
        wall.setHitbox(object.getShape());
        this.add(wall);
      } else if ("water".equals(object.type)) {
        var water = new Water(object.x, object.y);
        water.setHitbox(object.getShape());
        this.add(water);
      } else if ("item".equals(object.type)) {
        if (!GameState.get().items.contains(object.name)) {
          var item = new Item(object.name, object.x, object.y);
          this.add(item);
        }
      } else if ("enemy".equals(object.type)) {
        switch (object.name) {
          case "bamboo":
            {
              this.add(new Bamboo(object.x, object.y));
              break;
            }
        }
      }
    }

    if (PLAYER == null) {
      PLAYER = new Player(GameState.get().camX, GameState.get().camY);
    }
    if (PLAYER != null) {
      this.add(PLAYER);
    }

    GameState.get().map = mapFile;
    inventory = new Item();
    this.add(inventory);
  }

  public void run() {
    this.eraseAll();
    this.setColor(0, 0, 0);
    var camX = GameState.get().camX;
    var camY = GameState.get().camY;
    this.map.stampLayerToBackground("Floor", -camX, -camY);
    this.map.stampLayerToBackground("FloorObjects", -camX, -camY);
    this.map.stampLayerToBackground("Walls", -camX, -camY);

    var items = GameState.get().items;
    for (int i = 0; i < items.size(); i++) {
      inventory.setX(-this.getWidth() / 2 + 30);
      inventory.setY(this.getHeight() / 2 - 30 - i * 36);
      inventory.switchCostume(items.get(i));
      inventory.stampToForeground();
    }
  }
}
