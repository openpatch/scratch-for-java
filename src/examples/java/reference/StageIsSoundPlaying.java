package reference;
import org.openpatch.scratch.*;


public class StageIsSoundPlaying {
  public StageIsSoundPlaying() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    myStage.addSound("music", "jingles_STEEL16");
    myStage.playSound("music");
    while (true) {
      myStage.display("playing: " + myStage.isSoundPlaying("music"));
      myStage.wait(200);
    }
  }

  public static void main(String[] args) {
    new StageIsSoundPlaying();
  }
}
