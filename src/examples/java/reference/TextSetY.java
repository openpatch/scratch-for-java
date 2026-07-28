package reference;
import org.openpatch.scratch.*;


public class TextSetY {
  public TextSetY() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    while (true) {
      myText.setY(-80);
      myStage.wait(1000);
      myText.setY(80);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new TextSetY();
  }
}
