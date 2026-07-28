package reference;
import org.openpatch.scratch.*;


public class TextSetX {
  public TextSetX() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    while (true) {
      myText.setX(-200);
      myStage.wait(1000);
      myText.setX(200);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new TextSetX();
  }
}
