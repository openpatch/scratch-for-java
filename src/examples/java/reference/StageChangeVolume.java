package reference;
import org.openpatch.scratch.*;


public class StageChangeVolume {
  public StageChangeVolume() {
    Stage myStage = new Stage(600, 240);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
    while (myStage.getVolume() > 0) {
      myStage.changeVolume(-5);
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new StageChangeVolume();
  }
}
