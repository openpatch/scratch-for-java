package reference;
import org.openpatch.scratch.*;


public class SpriteGetTint {
  public SpriteGetTint() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.setTint(Color.fromHSB(78, 100, 100));
    Color tint = mySprite.getTint();
    System.out.println("hue " + tint.getHue() + ", red " + tint.getRed());
    // Colours are immutable, so the sprite is given a new tint each round.
    while (true) {
      tint = tint.withHue(tint.getHue() + 2);
      mySprite.setTint(tint);
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new SpriteGetTint();
  }
}
