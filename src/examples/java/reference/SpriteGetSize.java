package reference;
import org.openpatch.scratch.*;


public class SpriteGetSize {
  public SpriteGetSize() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.setSize(150);
    System.out.println("The sprite is drawn at " + mySprite.getSize() + " percent.");
  }

  public static void main(String[] args) {
    new SpriteGetSize();
  }
}
