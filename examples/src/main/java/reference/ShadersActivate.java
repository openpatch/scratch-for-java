package reference;
import org.openpatch.scratch.*;


public class ShadersActivate {
  public ShadersActivate() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.add(new Sprite("slime", "slimeGreen"));

    // the first shader added is active at once: the stage is gray
    myStage.getShaders().add("grayscale", "assets/grayscale.frag");
    myStage.getShaders().add("waves", "assets/waves.frag");

    while (true) {
      // while the mouse is down, the gray stage wobbles as well
      if (myStage.isMouseDown()) {
        myStage.getShaders().activate("waves");
      } else {
        myStage.getShaders().deactivate("waves");
      }
      myStage.waitSeconds(0.02);
    }
  }

  public static void main(String[] args) {
    new ShadersActivate();
  }
}
