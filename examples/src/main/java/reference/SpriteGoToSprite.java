package reference;
import org.openpatch.scratch.*;


public class SpriteGoToSprite {
  public SpriteGoToSprite() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite green = new Sprite("green", "slimeGreen");
    green.setPosition(-150, 60);
    myStage.add(green);

    Sprite blue = new Sprite("blue", "slimeBlue");
    myStage.add(blue);

    myStage.waitSeconds(1.5);
    // Straight to where the other sprite stands.
    blue.goToSprite(green);
  }

  public static void main(String[] args) {
    new SpriteGoToSprite();
  }
}
