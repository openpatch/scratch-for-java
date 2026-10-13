package reference;
import org.openpatch.scratch.*;


public class TextGetY {
  public TextGetY() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myText.setY(80);
    System.out.println("The text sits at y = " + myText.getY() + ".");
  }

  public static void main(String[] args) {
    new TextGetY();
  }
}
