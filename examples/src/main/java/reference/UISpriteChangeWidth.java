package reference;
import org.openpatch.scratch.*;


public class UISpriteChangeWidth {
  public UISpriteChangeWidth() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    Sprite myBar = new Sprite();
    myBar.setLayer(Layer.UI);
    myBar.addCostume("bar", "buttonLarge");
    myBar.setWidth(100);
    myStage.add(myBar);
    // A health bar filling up and emptying again.
    while (true) {
      while (myBar.getWidth() < 400) {
        myBar.changeWidth(10);
        myStage.waitSeconds(0.1);
      }
      while (myBar.getWidth() > 100) {
        myBar.changeWidth(-10);
        myStage.waitSeconds(0.1);
      }
    }
  }

  public static void main(String[] args) {
    new UISpriteChangeWidth();
  }
}
