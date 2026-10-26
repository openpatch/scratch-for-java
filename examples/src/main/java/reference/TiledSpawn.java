package reference;

import org.openpatch.scratch.*;
import org.openpatch.scratch.tiled.*;

/** @example.online false */
public class TiledSpawn {
  public static void main(String[] args) {
    Window window = new Window(400, 240);
    window.setStage(new TiledSpawnStage());
  }
}

class TiledSpawnStage extends Stage {
  TiledSpawnStage() {
    TiledMap map = new TiledMap("src/examples/java/reference/assets/tiled-data.tmx", this);
    map.spawn("Objects");
  }
}

class TiledSpawnPlayer extends Sprite {
  TiledSpawnPlayer(MapObject object) {
    this.addCostume("slimeGreen");
    this.say(object.getProperty("message"));
  }
}

class TiledSpawnWall extends Sprite {
  // The rectangle in Tiled supplies this sprite's hitbox.
}
