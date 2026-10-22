package reference;
import org.openpatch.scratch.*;


public class UISpriteSetHeight {
  public UISpriteSetHeight() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite myBar = new Sprite();
    myBar.setLayer(Layer.UI);
    myBar.addCostume("bar", "buttonLarge");
    myStage.add(myBar);
    while (true) {
      myBar.setHeight(40);
      myStage.waitSeconds(1);
      myBar.setHeight(120);
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new UISpriteSetHeight();
  }
}
