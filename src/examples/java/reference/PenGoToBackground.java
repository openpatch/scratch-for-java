package reference;
import org.openpatch.scratch.*;


public class PenGoToBackground {
  public PenGoToBackground() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Pen myPen = new Pen();
    myStage.add(myPen);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    myPen.setSize(20);
    myPen.setColor(0);
    myPen.down();
    // Behind the sprites: the drawing is covered by everything on the stage.
    myPen.goToBackground();
    while (true) {
      myPen.goToRandomPosition();
      myStage.wait(300);
    }
  }

  public static void main(String[] args) {
    new PenGoToBackground();
  }
}
