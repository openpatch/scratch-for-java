package reference;
import org.openpatch.scratch.*;


public class StageAddSound {
  public StageAddSound() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    // A sound on the stage plays wherever it is needed, without a sprite.
    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
  }

  public static void main(String[] args) {
    new StageAddSound();
  }
}
