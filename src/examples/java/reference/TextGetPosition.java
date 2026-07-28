package reference;
import org.openpatch.scratch.*;


public class TextGetPosition {
  public TextGetPosition() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setPosition(-150, 60);
    System.out.println("The text sits at " + myText.getPosition() + ".");
  }

  public static void main(String[] args) {
    new TextGetPosition();
  }
}
