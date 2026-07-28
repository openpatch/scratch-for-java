package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraSetPosition {
  public CameraSetPosition() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // The camera looks at this point on the stage.
    while (true) {
      myCamera.setPosition(-150, 0);
      myStage.wait(1000);
      myCamera.setPosition(150, 0);
      myStage.wait(1000);
      // A vector works too.
      myCamera.setPosition(new Vector2(0, 80));
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new CameraSetPosition();
  }
}
