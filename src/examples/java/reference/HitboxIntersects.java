package reference;
import org.openpatch.scratch.*;


public class HitboxIntersects {
  public HitboxIntersects() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
