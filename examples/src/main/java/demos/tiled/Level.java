package demos.tiled;

import org.openpatch.scratch.Window;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.tiled.MapObject;
import org.openpatch.scratch.tiled.TiledMap;

public class Level extends Stage {

  // begin stage designer
  private UIItem inventory;
  // end stage designer

  private TiledMap map;
  private Player player;
  public Level(String mapFile, Player player) {
    map = new TiledMap("demos/tiled/" + mapFile + ".tmx", this);
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
