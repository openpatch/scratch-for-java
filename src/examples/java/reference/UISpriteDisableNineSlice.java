package reference;
import org.openpatch.scratch.*;


public class UISpriteDisableNineSlice {
  public UISpriteDisableNineSlice() {
    Stage myStage = new Stage(600, 240);
    UISprite myPanel = new UISprite();
    myPanel.addCostume("panel", "buttonLarge");
    myPanel.setWidth(420);
    myStage.add(myPanel);
    // With nine-slicing the corners keep their size, without it the whole
    // costume is stretched.
    while (true) {
      myPanel.setNineSlice(6, 6, 6, 6);
      myStage.wait(1500);
      myPanel.disableNineSlice();
      myStage.wait(1500);
    }
  }

  public static void main(String[] args) {
    new UISpriteDisableNineSlice();
  }
}
