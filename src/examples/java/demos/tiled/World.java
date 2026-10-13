package demos.tiled;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.MapObject;
import org.openpatch.scratch.extensions.tiled.TiledMap;

public class World extends Stage {

  // begin stage designer
  private UIItem inventory;
  // end stage designer

  private TiledMap map;
  private Player player;
  public World(String mapFile, Player player) {
    map = new TiledMap("demos/tiled/" + mapFile + ".tmx", this);
    // Every object whose type is a sprite class becomes that sprite:
    // walls, water, warps, items and enemies.
    for (Sprite sprite : map.spawn("Objects")) {
      // an item that was picked up stays picked up
      if (sprite instanceof Item item && GameState.get().items.contains(item.getName())) {
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
      this.getCamera().setPosition(player.getX(), player.getY());
    }
    this.add(player);

    GameState.get().map = mapFile;
    // begin stage designer
    inventory = new UIItem();
    this.add(inventory);
    // end stage designer
    this.player = player;

  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.F1) {
      this.setDebug(!this.isDebug());
    } else if (keyCode == KeyCode.F5) {
      GameState.save();
      this.display("Game saved", 2000);
    }
  }

  public void run() {
    this.eraseAll();
    this.setColor(0, 0, 0);

    this.getCamera().setPosition(player.getPosition());

    this.map.stampLayerToBackground("Floor");
    this.map.stampLayerToBackground("FloorObjects");
    this.map.stampLayerToBackground("Walls");

    if (this.isKeyPressed(KeyCode.DIGIT_1)) {
      this.getCamera().changeZoom(1);
    }
    if (this.isKeyPressed(KeyCode.DIGIT_0)) {
      this.getCamera().changeZoom(-1);
    }

    var items = GameState.get().items;
    for (int i = 0; i < items.size(); i++) {
      inventory.setX(-this.getWidth() / 2 + 30);
      inventory.setY(this.getHeight() / 2 - 30 - i * 36);
      inventory.switchCostume(items.get(i));
      inventory.stamp(Layer.UI);
    }
  }

  public Player getPlayer() {
    return player;
  }
}
