package reference;
import org.openpatch.scratch.*;

public class StageSetCursor {

  public StageSetCursor() {
    Stage myStage = new Stage(600, 240);
    // The cursor can be a picture next to the program, or the name of a
    // built-in sprite. The second pair of numbers is the point of the picture
    // that does the pointing - here the middle of the 128 by 128 jewel.
    while (true) {
      if (myStage.getMouseX() < 0) {
        myStage.setCursor("hudJewel_blue");
      } else {
        myStage.setCursor("hudJewel_red", 22, 20);
      }
      myStage.display("Mouse: " + myStage.getMouseX() + ", " + myStage.getMouseY());
      myStage.wait(16);
    }
  }

  public static void main(String[] args) {
    new StageSetCursor();
  }
}
