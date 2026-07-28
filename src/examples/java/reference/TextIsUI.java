package reference;
import org.openpatch.scratch.*;


public class TextIsUI {
  public TextIsUI() {
    Stage myStage = new Stage(600, 240);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    System.out.println("Moves with the camera: " + myText.isUI());
    myText.setIsUI(true);
    System.out.println("Now a part of the interface: " + myText.isUI());
  }

  public static void main(String[] args) {
    new TextIsUI();
  }
}
