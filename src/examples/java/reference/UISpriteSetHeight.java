package reference;
import org.openpatch.scratch.*;


public class UISpriteSetHeight {
  public UISpriteSetHeight() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    UISprite myBar = new UISprite();
    myBar.addCostume("bar", "buttonLarge");
    myStage.add(myBar);
    while (true) {
      myBar.setHeight(40);
      myStage.wait(1000);
      myBar.setHeight(120);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new UISpriteSetHeight();
  }
}
