package reference;
import org.openpatch.scratch.*;

public class TextGoToFrontLayer {
  public TextGoToFrontLayer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Text hello = new Text("Hello", -25, 12, 200);
    hello.setStyle(TextStyle.BOX);
    hello.setBackgroundColor(250, 240, 180);
    myStage.add(hello);

    // Added last, so it covers the one added before it.
    Text world = new Text("World", 25, -12, 200);
    world.setStyle(TextStyle.BOX);
    world.setBackgroundColor(180, 220, 250);
    myStage.add(world);

    while (true) {
      myStage.waitSeconds(1.2);
      hello.goToFrontLayer();
      myStage.waitSeconds(1.2);
      world.goToFrontLayer();
    }
  }

  public static void main(String[] args) {
    new TextGoToFrontLayer();
  }
}
