package reference;
import org.openpatch.scratch.*;


public class StageSetVolume {
  public StageSetVolume() {
    Stage myStage = new Stage(600, 240);

    myStage.addSound("music", "jingles_STEEL16");
    // Half as loud. This is the volume of the stage, not of the sprites on it.
    myStage.setVolume(50);
    myStage.playSound("music");
  }

  public static void main(String[] args) {
    new StageSetVolume();
  }
}
