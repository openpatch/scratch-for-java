package Tiled;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.TiledMap;

public class World extends Stage {

  private TiledMap map;
  public static Player PLAYER;
  private Item inventory;

  public World() {
    GameState.load();
    this.setDebug(true);
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
    map = new TiledMap("Tiled/" + mapFile + ".tmx", this);
    for (var object : map.getObjectsFromLayer("Objects")) {
      // Spawn Points should only have an effect, if there was no PLAYER object. Meaning the first
      // time a map is loading.
      if ("spawn-point".equals(object.type) && PLAYER == null) {
        var playerX = GameState.get().playerX;
        // check playerX and playerY are not saved in GameState
        if (playerX == Double.MAX_VALUE) {
          GameState.get().playerX = object.x;
          GameState.get().playerY = object.y;
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
      PLAYER = new Player(GameState.get().playerX, GameState.get().playerY);
      this.getCamera().setPosition(PLAYER.getX(), PLAYER.getY());
    }
    if (PLAYER != null) {
      this.add(PLAYER);
    }

    GameState.get().map = mapFile;
    inventory = new Item();
    inventory.isUI(true);
    this.add(inventory);
  }

  public void run() {
    this.eraseAll();
    this.setColor(0, 0, 0);

    this.getCamera().setPosition(PLAYER.getPosition());

    this.map.stampLayerToBackground("Floor");
    this.map.stampLayerToBackground("FloorObjects");
    this.map.stampLayerToBackground("Walls");

    if (this.isKeyPressed(KeyCode.VK_1)) {
      this.getCamera().changeZoom(1);
    }
    if (this.isKeyPressed(KeyCode.VK_0)) {
      this.getCamera().changeZoom(-1);
    }

    var items = GameState.get().items;
    for (int i = 0; i < items.size(); i++) {
      inventory.setX(-this.getWidth() / 2 + 30);
      inventory.setY(this.getHeight() / 2 - 30 - i * 36);
      inventory.switchCostume(items.get(i));
      inventory.stampToUI();
    }
  }
}
