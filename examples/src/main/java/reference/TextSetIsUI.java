package reference;
import org.openpatch.scratch.*;


public class TextSetIsUI {
  public TextSetIsUI() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Text myText = new Text("Hello World", 0, 0, 400);
    myStage.add(myText);

    myStage.getCamera().setZoom(2);
    // A UI text ignores the camera, so it stays put and keeps its size while
    // everything else is zoomed in.
    myText.setLayer(Layer.UI);
  }

  public static void main(String[] args) {
    new TextSetIsUI();
  }
}
