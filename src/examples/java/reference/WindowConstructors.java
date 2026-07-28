package reference;
import org.openpatch.scratch.*;


public class WindowConstructors {
  public WindowConstructors() {
    // A window of a given size, holding one stage at a time.
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myStage.add(new Sprite("slime", "slimeGreen"));
    myWindow.setStage(myStage);
  }

  public static void main(String[] args) {
    new WindowConstructors();
  }
}
