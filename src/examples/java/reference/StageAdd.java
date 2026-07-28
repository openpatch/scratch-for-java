package reference;
import org.openpatch.scratch.*;


public class StageAdd {
  public StageAdd() {
    Stage myStage = new Stage(600, 240);

    // Sprites, texts and pens all go onto the stage the same way, and are drawn
    // in the order they were added.
    Pen myPen = new Pen();
    myStage.add(myPen);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    Text myText = new Text("Hello World", 0, 90, 400);
    myStage.add(myText);
  }

  public static void main(String[] args) {
    new StageAdd();
  }
}
