package reference;
import org.openpatch.scratch.*;


public class UISpriteDisableNineSlice {
  public UISpriteDisableNineSlice() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
