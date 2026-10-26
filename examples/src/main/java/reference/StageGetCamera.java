package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class StageGetCamera {
  public StageGetCamera() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // The camera decides which part of the stage is on screen and how close.
    Camera myCamera = myStage.getCamera();
    while (true) {
      myCamera.changeZoom(0.01);
      myCamera.changeX(1);
      myStage.waitSeconds(0.05);
    }
  }

  public static void main(String[] args) {
    new StageGetCamera();
  }
}
