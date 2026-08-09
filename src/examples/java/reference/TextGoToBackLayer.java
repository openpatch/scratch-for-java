package reference;
import org.openpatch.scratch.*;

public class TextGoToBackLayer {
  public TextGoToBackLayer() {
    Stage myStage = new Stage(600, 240);

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
      myStage.wait(1200);
      world.goToBackLayer();
      myStage.wait(1200);
      hello.goToBackLayer();
    }
  }

  public static void main(String[] args) {
    new TextGoToBackLayer();
  }
}
