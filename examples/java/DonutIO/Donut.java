package DonutIO;

import java.awt.geom.Ellipse2D;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.extensions.math.Random;
import org.openpatch.scratch.extensions.math.Vector2;

public class Donut extends Sprite {

  private int strength;
  protected double speed = 1;
  protected Vector2 mapPosition;

  public Donut() {
    this(0, 0, 2);
  }

  public Donut(double mapX, double mapY, int strength) {
    this.addCostume("donut", "DonutIO/assets/donut.png");
    this.setHitbox(new Ellipse2D.Double(0, 0, 512, 480));

    this.setMapPosition(new Vector2(mapX, mapY));

    this.setStrength(strength);

    this.setTint(Random.randomInt(240), Random.randomInt(240), Random.randomInt(240));
  }

  public void setStrength(int strength) {
    this.strength = strength;
    this.setSize(strength);
  }

  public void setMapPosition(Vector2 position) {
    this.mapPosition = position;
    this.setPosition(this.mapPosition.sub(WorldStage.CAM));
  }

  public void run() {
    this.setPosition(this.mapPosition.sub(WorldStage.CAM));

    var touchingDonut = this.getTouchingSprite(Donut.class);
    if (touchingDonut != null && this.strength >= touchingDonut.strength) {
      this.setStrength(this.strength + touchingDonut.strength);
      touchingDonut.remove();
    }
  }
}
