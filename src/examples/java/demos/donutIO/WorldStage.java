package demos.donutIO;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.math.Vector2;

public class WorldStage extends Stage {

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

    if (this.isKeyPressed(KeyCode.VK_1)) {
      this.getCamera().changeZoom(1);
      manualZoom = true;
    }
    if (this.isKeyPressed(KeyCode.VK_0)) {
      this.getCamera().changeZoom(-1);
      manualZoom = true;
    }
    if (this.isKeyPressed(KeyCode.VK_R)) {
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
