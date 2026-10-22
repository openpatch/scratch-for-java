package reference;
import org.openpatch.scratch.*;


public class WindowGetStage {
  public WindowGetStage() {
    Window myWindow = new Window(600, 240);
    myWindow.setStage(new Stage());
    // The stage that is on screen right now.
    myWindow.getStage().add(new Sprite("slime", "slimeGreen"));
  }

  public static void main(String[] args) {
    new WindowGetStage();
  }
}
