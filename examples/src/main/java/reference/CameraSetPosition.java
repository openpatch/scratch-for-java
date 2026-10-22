package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraSetPosition {
  public CameraSetPosition() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // The camera looks at this point on the stage.
    while (true) {
      myCamera.setPosition(-150, 0);
      myStage.waitSeconds(1);
      myCamera.setPosition(150, 0);
      myStage.waitSeconds(1);
      // A vector works too.
      myCamera.setPosition(new Vector2(0, 80));
      myStage.waitSeconds(1);
    }
  }

  public static void main(String[] args) {
    new CameraSetPosition();
  }
}
