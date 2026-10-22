package reference;
import org.openpatch.scratch.*;


public class TextSetX {
  public TextSetX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    while (true) {
      myText.setX(-200);
      myStage.waitSeconds(1);
      myText.setX(200);
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new TextSetX();
  }
}
