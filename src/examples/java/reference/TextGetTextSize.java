package reference;
import org.openpatch.scratch.*;


public class TextGetTextSize {
  public TextGetTextSize() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setTextSize(32);
    System.out.println("The text is " + myText.getTextSize() + " pixels high.");
  }

  public static void main(String[] args) {
    new TextGetTextSize();
  }
}
