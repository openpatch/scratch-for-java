package reference;
import org.openpatch.scratch.*;

public class SpriteIsTouchingColor {
  public SpriteIsTouchingColor() {
    Stage myStage = new Stage(600, 240);

    // a red line across the stage
    Sprite painter = new Sprite();
    myStage.add(painter);
    painter.getPen().setColor(HtmlColor.RED);
    painter.getPen().setSize(20);
    painter.setPosition(-300, 0);
    painter.getPen().down();
    painter.setPosition(300, 0);
    painter.getPen().up();

    Sprite slime = new Sprite("slime", "slimeGreen");
    slime.setSize(50);
    myStage.add(slime);
    while (true) {
      slime.goToMousePointer();
      if (slime.isTouchingColor(HtmlColor.RED)) {
        slime.say("Ouch!");
      } else {
        slime.say(null);
      }
    }
  }

  public static void main(String[] args) {
    new SpriteIsTouchingColor();
  }
}
