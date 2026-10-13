package reference;
import org.openpatch.scratch.*;


public class SpriteConstructors {
  public SpriteConstructors() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    // A sprite with a name and a first costume.
    Sprite green = new Sprite("green", "slimeGreen");
    green.setX(-120);
    myStage.add(green);

    // A sprite without a costume draws a cross until it is given one.
    Sprite empty = new Sprite();
    myStage.add(empty);

    // A copy of a sprite, with the same costumes and the same size.
    Sprite copy = new Sprite(green);
    copy.setX(120);
    myStage.add(copy);
  }

  public static void main(String[] args) {
    new SpriteConstructors();
  }
}
