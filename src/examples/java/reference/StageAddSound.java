package reference;
import org.openpatch.scratch.*;


public class StageAddSound {
  public StageAddSound() {
    Stage myStage = new Stage(600, 240);

    // A sound on the stage plays wherever it is needed, without a sprite.
    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
  }

  public static void main(String[] args) {
    new StageAddSound();
  }
}
