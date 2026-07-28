package reference;
import org.openpatch.scratch.*;


public class UISpriteSetNineSlice {
  public UISpriteSetNineSlice() {
    Stage myStage = new Stage(600, 240);
    UISprite myPanel = new UISprite();
    myPanel.addCostume("panel", "buttonLarge");
    myStage.add(myPanel);
    // Nine-slicing keeps the corners of the costume their own size and stretches
    // only the middle, so a button can grow without its rounded corners smearing.
    myPanel.setNineSlice(6, 6, 6, 6);
    while (true) {
      myPanel.setWidth(150);
      myStage.wait(1200);
      myPanel.setWidth(420);
      myStage.wait(1200);
    }
  }

  public static void main(String[] args) {
    new UISpriteSetNineSlice();
  }
}
