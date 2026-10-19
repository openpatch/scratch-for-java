package demos.tiled;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.tiled.MapObject;

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
      }, 500);
    }
  }
}
