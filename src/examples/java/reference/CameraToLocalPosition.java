package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraToLocalPosition {
  public CameraToLocalPosition() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setPosition(100, 0);
    // From a place on the stage to the place on the screen it is drawn at.
    Vector2 onStage = new Vector2(100, 0);
    System.out.println(onStage + " is drawn at " + myCamera.toLocalPosition(onStage));
  }

  public static void main(String[] args) {
    new CameraToLocalPosition();
  }
}
