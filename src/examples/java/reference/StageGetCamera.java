package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class StageGetCamera {
  public StageGetCamera() {
    Stage myStage = new Stage(600, 240);

    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    // The camera decides which part of the stage is on screen and how close.
    Camera myCamera = myStage.getCamera();
    while (true) {
      myCamera.changeZoom(0.01);
      myCamera.changeX(1);
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new StageGetCamera();
  }
}
