package reference;
import org.openpatch.scratch.*;


public class SpriteGetText {
  public SpriteGetText() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.say("Hello");
    // The speech bubble is a Text of its own, and can be styled like one.
    Text bubble = mySprite.getText();
    bubble.setTextColor(220, 40, 40);
    bubble.setTextSize(24);
  }

  public static void main(String[] args) {
    new SpriteGetText();
  }
}
