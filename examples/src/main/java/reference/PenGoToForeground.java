package reference;
import org.openpatch.scratch.*;


public class PenGoToForeground {
  public PenGoToForeground() {
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
    // In front of the sprites: the drawing covers everything on the stage.
    myPen.setLayer(Layer.FOREGROUND);
    while (true) {
      myPen.goToRandomPosition();
      myStage.waitSeconds(0.3);
    }
  }

  public static void main(String[] args) {
    new PenGoToForeground();
  }
}
