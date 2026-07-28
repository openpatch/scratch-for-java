package reference;
import org.openpatch.scratch.*;


public class UISpriteSetHeight {
  public UISpriteSetHeight() {
    Stage myStage = new Stage(600, 240);
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
