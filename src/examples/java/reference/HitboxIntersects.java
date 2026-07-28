package reference;
import org.openpatch.scratch.*;


public class HitboxIntersects {
  public HitboxIntersects() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite green = new Sprite("green", "slimeGreen");
    green.setX(-150);
    myStage.add(green);

    Sprite blue = new Sprite("blue", "slimeBlue");
    myStage.add(blue);

    // Whether two hitboxes overlap, which is what isTouchingSprite() asks.
    while (true) {
      green.changeX(4);
      green.ifOnEdgeBounce();
      myStage.display("touching: " + green.getHitbox().intersects(blue.getHitbox()));
      myStage.wait(30);
    }
  }

  public static void main(String[] args) {
    new HitboxIntersects();
  }
}
