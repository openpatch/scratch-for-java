package reference;
import org.openpatch.scratch.*;


public class PenGoToForeground {
  public PenGoToForeground() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    myPen.setSize(20);
    myPen.setColor(0);
    myPen.down();
    // In front of the sprites: the drawing covers everything on the stage.
    myPen.goToForeground();
    while (true) {
      myPen.goToRandomPosition();
      myStage.wait(300);
    }
  }

  public static void main(String[] args) {
    new PenGoToForeground();
  }
}
