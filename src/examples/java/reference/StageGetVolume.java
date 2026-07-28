package reference;
import org.openpatch.scratch.*;


public class StageGetVolume {
  public StageGetVolume() {
    Stage myStage = new Stage(600, 240);

    myStage.setVolume(75);
    System.out.println("The stage plays at " + myStage.getVolume() + " percent.");
  }

  public static void main(String[] args) {
    new StageGetVolume();
  }
}
