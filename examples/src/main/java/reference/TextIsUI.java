package reference;
import org.openpatch.scratch.*;


public class TextIsUI {
  public TextIsUI() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    System.out.println("Moves with the camera: " + myText.getLayer().equals(Layer.UI));
    myText.setLayer(Layer.UI);
    System.out.println("Now a part of the interface: " + myText.getLayer().equals(Layer.UI));
  }

  public static void main(String[] args) {
    new TextIsUI();
  }
}
