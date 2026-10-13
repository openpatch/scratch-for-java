package reference;
import org.openpatch.scratch.*;


public class SpriteGetTint {
  public SpriteGetTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.setTint(200);
    Color tint = mySprite.getTint();
    System.out.println("hue " + tint.getH() + ", red " + tint.getRed());
    // The colour that comes back is the sprite's own, so changing it tints the
    // sprite as well.
    while (true) {
      tint.changeColor(2);
      mySprite.setTint(tint);
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new SpriteGetTint();
  }
}
