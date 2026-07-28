package reference;
import org.openpatch.scratch.*;


public class UISpriteConstructors {
  public UISpriteConstructors() {
    Stage myStage = new Stage(600, 240);
    myStage.getCamera().setZoom(2);
    // A UI sprite ignores the camera, which is what makes it useful for a
    // button or a health bar: it keeps its place and its size while the world
    // behind it moves.
    UISprite myButton = new UISprite();
    myButton.addCostume("button", "buttonLarge");
    myButton.setPosition(0, -80);
    myStage.add(myButton);
  }

  public static void main(String[] args) {
    new UISpriteConstructors();
  }
}
