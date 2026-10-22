package reference;
import org.openpatch.scratch.*;


public class StageGetShaders {
  public StageGetShaders() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    myStage.addBackdrop("forest", "background");
    myStage.add(new Sprite("slime", "slimeGreen"));

    // a stage shader draws everything on the stage: backdrop, pen and sprites
    Shader waves = myStage.getShaders().add("waves", "assets/waves.frag");

    while (true) {
      myStage.waitSeconds(0.02);
    }
  }

  public static void main(String[] args) {
    new StageGetShaders();
  }
}
