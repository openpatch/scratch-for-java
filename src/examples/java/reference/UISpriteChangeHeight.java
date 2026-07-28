package reference;
import org.openpatch.scratch.*;


public class UISpriteChangeHeight {
  public UISpriteChangeHeight() {
    Stage myStage = new Stage(600, 240);
    UISprite myBar = new UISprite();
    myBar.addCostume("bar", "buttonLarge");
    myBar.setHeight(20);
    myStage.add(myBar);
    while (true) {
      while (myBar.getHeight() < 160) {
        myBar.changeHeight(4);
        myStage.wait(100);
      }
      while (myBar.getHeight() > 20) {
        myBar.changeHeight(-4);
        myStage.wait(100);
      }
    }
  }

  public static void main(String[] args) {
    new UISpriteChangeHeight();
  }
}
