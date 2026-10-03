package reference;
import org.openpatch.scratch.*;

public class SpriteIsColorTouchingColor {
  public SpriteIsColorTouchingColor() {
    Stage myStage = new Stage(600, 240);

    // a blue line across the stage
    Sprite painter = new Sprite();
    myStage.add(painter);
    painter.getPen().setColor(HtmlColor.BLUE);
    painter.getPen().setSize(20);
    painter.setPosition(-300, 0);
    painter.getPen().down();
    painter.setPosition(300, 0);
    painter.getPen().up();

    // The bee's stripes are this yellow; its wings are white.
    Color yellow = new Color("#ffcc00");
    Sprite bee = new Sprite("bee", "bee");
    bee.setSize(60);
    myStage.add(bee);
    while (true) {
      bee.goToMousePointer();
      if (bee.isColorTouchingColor(yellow, HtmlColor.BLUE)) {
        bee.say("My stripes are wet!");
      } else {
        bee.say(null);
      }
    }
  }

  public static void main(String[] args) {
    new SpriteIsColorTouchingColor();
  }
}
