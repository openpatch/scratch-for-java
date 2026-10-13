package reference;
import org.openpatch.scratch.*;


public class TextGetWidth {
  public TextGetWidth() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setWidth(250);
    System.out.println("The text may grow to " + myText.getWidth() + " pixels.");
  }

  public static void main(String[] args) {
    new TextGetWidth();
  }
}
