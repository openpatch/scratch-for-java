package reference;
import org.openpatch.scratch.*;


public class TextSetStyle {
  public TextSetStyle() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    // The four ways a text can look.
    while (true) {
      myText.setStyle(TextStyle.PLAIN);
      myStage.waitSeconds(1.5);
      myText.setStyle(TextStyle.BOX);
      myStage.waitSeconds(1.5);
      myText.setStyle(TextStyle.SPEAK);
      myStage.waitSeconds(1.5);
      myText.setStyle(TextStyle.THINK);
      myStage.waitSeconds(1.5);
    }
  }

  public static void main(String[] args) {
    new TextSetStyle();
  }
}
