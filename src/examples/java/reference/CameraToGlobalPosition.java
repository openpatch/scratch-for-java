package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraToGlobalPosition {
  public CameraToGlobalPosition() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setPosition(100, 0);
    // From a place on the screen back to the place on the stage - which is what
    // turns a mouse position into a place in the world.
    Vector2 onScreen = new Vector2(0, 0);
    System.out.println("the middle of the screen is " + myCamera.toGlobalPosition(onScreen));
  }

  public static void main(String[] args) {
    new CameraToGlobalPosition();
  }
}
