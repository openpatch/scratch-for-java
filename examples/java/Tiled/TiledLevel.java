import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.tiled.TiledMap;

public class TiledLevel extends Stage {

  TiledMap map;
  double camX;
  double camY;

  public TiledLevel() {
    map = new TiledMap("Map.tmx", this);
    for (var object : map.getObjectsFromLayer("Objects")) {
      if ("spawn-point".equals(object.type)) {
        camX = object.x;
        camY = object.y;
      }
    }
  }

  public void run() {

    if (isKeyPressed(KeyCode.VK_W)) {
      camY += 1;
    }
    if (isKeyPressed(KeyCode.VK_S)) {
      camY -= 1;
    }
    if (isKeyPressed(KeyCode.VK_A)) {
      camX -= 1;
    }
    if (isKeyPressed(KeyCode.VK_D)) {
      camX += 1;
    }

    this.eraseAll();
    this.map.stampLayerToBackground("Floor", -camX, -camY);
    this.map.stampLayerToBackground("Ground", -camX, -camY);
  }
}
