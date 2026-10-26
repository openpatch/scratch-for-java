package reference;
import org.openpatch.scratch.*;


public class StageGetVolume {
  public StageGetVolume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.setVolume(75);
    System.out.println("The stage plays at " + myStage.getVolume() + " percent.");
  }

  public static void main(String[] args) {
    new StageGetVolume();
  }
}
