package reference;
import org.openpatch.scratch.*;


public class WindowSetDebug {
  public WindowSetDebug() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myStage.add(new Sprite("slime", "slimeGreen"));
    myWindow.setStage(myStage);
    // Debug mode for every stage in the window at once.
    myWindow.setDebug(true);
  }

  public static void main(String[] args) {
    new WindowSetDebug();
  }
}
