package reference;
import org.openpatch.scratch.*;


public class UISpriteSetWidth {
  public UISpriteSetWidth() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    UISprite myBar = new UISprite();
    myBar.addCostume("bar", "buttonLarge");
    myStage.add(myBar);
    // A UI sprite is measured in pixels rather than scaled in percent.
    while (true) {
      myBar.setWidth(120);
      myStage.wait(1000);
      myBar.setWidth(360);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new UISpriteSetWidth();
  }
}
