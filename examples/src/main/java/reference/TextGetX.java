package reference;
import org.openpatch.scratch.*;


public class TextGetX {
  public TextGetX() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setX(-150);
    System.out.println("The text sits at x = " + myText.getX() + ".");
  }

  public static void main(String[] args) {
    new TextGetX();
  }
}
