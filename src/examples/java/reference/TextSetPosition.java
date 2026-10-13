package reference;
import org.openpatch.scratch.*;


public class TextSetPosition {
  public TextSetPosition() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myStage.wait(1000);
    myText.setPosition(-150, 60);
    myStage.wait(1000);
    // The same thing with a vector.
    myText.setPosition(new Vector2(150, -60));
  }

  public static void main(String[] args) {
    new TextSetPosition();
  }
}
