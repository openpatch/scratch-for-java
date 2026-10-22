package reference;
import org.openpatch.scratch.*;


public class StageChangeVolume {
  public StageChangeVolume() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
    while (myStage.getVolume() > 0) {
      myStage.changeVolume(-5);
      myStage.waitSeconds(0.2);
    }
  }

  public static void main(String[] args) {
    new StageChangeVolume();
  }
}
