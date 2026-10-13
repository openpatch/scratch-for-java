package reference;
import org.openpatch.scratch.*;


public class StageStopSound {
  public StageStopSound() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
    myStage.wait(2000);
    myStage.stopSound("music");
  }

  public static void main(String[] args) {
    new StageStopSound();
  }
}
