package reference;
import org.openpatch.scratch.*;


public class UISpriteChangeWidth {
  public UISpriteChangeWidth() {
    Stage myStage = new Stage(600, 240);
    UISprite myBar = new UISprite();
    myBar.addCostume("bar", "buttonLarge");
    myBar.setWidth(100);
    myStage.add(myBar);
    // A health bar filling up and emptying again.
    while (true) {
      while (myBar.getWidth() < 400) {
        myBar.changeWidth(10);
        myStage.wait(100);
      }
      while (myBar.getWidth() > 100) {
        myBar.changeWidth(-10);
        myStage.wait(100);
      }
    }
  }

  public static void main(String[] args) {
    new UISpriteChangeWidth();
  }
}
