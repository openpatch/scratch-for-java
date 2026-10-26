package reference;
import org.openpatch.scratch.*;


public class UISpriteSetNineSlice {
  public UISpriteSetNineSlice() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite myPanel = new Sprite();
    myPanel.setLayer(Layer.UI);
    myPanel.addCostume("panel", "buttonLarge");
    myStage.add(myPanel);
    // Nine-slicing keeps the corners of the costume their own size and stretches
    // only the middle, so a button can grow without its rounded corners smearing.
    myPanel.setNineSlice(6, 6, 6, 6);
    while (true) {
      myPanel.setWidth(150);
      myStage.waitSeconds(1.2);
      myPanel.setWidth(420);
      myStage.waitSeconds(1.2);
    }
  }

  public static void main(String[] args) {
    new UISpriteSetNineSlice();
  }
}
