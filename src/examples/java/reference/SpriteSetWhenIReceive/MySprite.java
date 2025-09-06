package reference.SpriteSetWhenIReceive;

import org.openpatch.scratch.Sprite;

public class MySprite extends Sprite {
  public MySprite() {
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    
    // Set a custom message handler
    this.setWhenIReceive((sprite, message) -> {
      sprite.say("Received: " + message);
    });
  }

  public void run() {
    // Broadcast a message after 1 second
    if (this.getTimer().forMillis(1000)) {
      this.broadcast("Hello World!");
    }
  }
}