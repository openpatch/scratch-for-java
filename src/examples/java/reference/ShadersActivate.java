package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.shader.*;


public class ShadersActivate {
  public ShadersActivate() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.add(new Sprite("slime", "slimeGreen"));

    // the first shader added is active at once: the stage is gray
    myStage.getShaders().add("grayscale", "assets/grayscale.frag", null);
    Shader waves = myStage.getShaders().add("waves", "assets/waves.frag", null);

    while (true) {
      // while the mouse is down, the gray stage wobbles as well
      if (myStage.isMouseDown()) {
        myStage.getShaders().activate("waves");
      } else {
        myStage.getShaders().deactivate("waves");
      }
      waves.set("time", Timer.millis() / 1000.0);
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new ShadersActivate();
  }
}
