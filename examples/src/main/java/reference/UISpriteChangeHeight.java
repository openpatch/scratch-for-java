package reference;
import org.openpatch.scratch.*;


public class UISpriteChangeHeight {
  public UISpriteChangeHeight() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite myBar = new Sprite();
    myBar.setLayer(Layer.UI);
    myBar.addCostume("bar", "buttonLarge");
    myBar.setHeight(20);
    myStage.add(myBar);
    while (true) {
      while (myBar.getHeight() < 160) {
        myBar.changeHeight(4);
        myStage.waitSeconds(0.1);
      }
      while (myBar.getHeight() > 20) {
        myBar.changeHeight(-4);
        myStage.waitSeconds(0.1);
      }
    }
  }

  public static void main(String[] args) {
    new UISpriteChangeHeight();
  }
}
