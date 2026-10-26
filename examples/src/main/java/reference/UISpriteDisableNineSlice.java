package reference;
import org.openpatch.scratch.*;


public class UISpriteDisableNineSlice {
  public UISpriteDisableNineSlice() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite myPanel = new Sprite();
    myPanel.setLayer(Layer.UI);
    myPanel.addCostume("panel", "buttonLarge");
    myPanel.setWidth(420);
    myStage.add(myPanel);
    // With nine-slicing the corners keep their size, without it the whole
    // costume is stretched.
    while (true) {
      myPanel.setNineSlice(6, 6, 6, 6);
      myStage.waitSeconds(1.5);
      myPanel.disableNineSlice();
      myStage.waitSeconds(1.5);
    }
  }

  public static void main(String[] args) {
    new UISpriteDisableNineSlice();
  }
}
