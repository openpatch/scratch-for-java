package reference;
import org.openpatch.scratch.*;


public class HitboxConstructors {
  public HitboxConstructors() {
    Stage myStage = new Stage(600, 240);
    myStage.setDebug(true);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // A hitbox of its own, from a shape or from a list of corners. A sprite
    // builds one from its costume, so this is only needed to check a shape by
    // hand.
    Hitbox fromShape = new Hitbox(new Circle(0, 0, 30));
    double[] x = { -20, 20, 20, -20 };
    double[] y = { -20, -20, 20, 20 };
    Hitbox fromPoints = new Hitbox(x, y);
    System.out.println("they overlap: " + fromShape.intersects(fromPoints));
  }

  public static void main(String[] args) {
    new HitboxConstructors();
  }
}
