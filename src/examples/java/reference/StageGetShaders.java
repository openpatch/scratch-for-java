package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.shader.*;


public class StageGetShaders {
  public StageGetShaders() {
    Stage myStage = new Stage(600, 240);
    myStage.addBackdrop("forest", "background");
    myStage.add(new Sprite("slime", "slimeGreen"));

    // a stage shader draws everything on the stage: backdrop, pen and sprites
    Shader waves = myStage.getShaders().add("waves", "assets/waves.frag", null);

    while (true) {
      waves.set("time", Timer.millis() / 1000.0);
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new StageGetShaders();
  }
}
