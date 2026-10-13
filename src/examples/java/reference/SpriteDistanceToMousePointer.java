package reference;
import org.openpatch.scratch.*;

public class SpriteDistanceToMousePointer {
  public SpriteDistanceToMousePointer() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite gamma = new Sprite("gamma", "slimePurple");
    gamma.setPosition(0, 50);
    myStage.add(gamma);
    while (true) {
      gamma.changeX(5);
      myStage.display("Distance: " + gamma.distanceToMousePointer());
      myStage.wait(100);
    }
  }

  public static void main(String[] args) {
    new SpriteDistanceToMousePointer();
  }
}
