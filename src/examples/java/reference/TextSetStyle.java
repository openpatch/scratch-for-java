package reference;
import org.openpatch.scratch.*;


public class TextSetStyle {
  public TextSetStyle() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    // The four ways a text can look.
    while (true) {
      myText.setStyle(TextStyle.PLAIN);
      myStage.wait(1500);
      myText.setStyle(TextStyle.BOX);
      myStage.wait(1500);
      myText.setStyle(TextStyle.SPEAK);
      myStage.wait(1500);
      myText.setStyle(TextStyle.THINK);
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new TextSetStyle();
  }
}
