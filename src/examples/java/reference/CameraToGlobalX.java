package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraToGlobalX {
  public CameraToGlobalX() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setPosition(100, 0);
    System.out.println("the middle of the screen is x = " + myCamera.toGlobalX(0));
  }

  public static void main(String[] args) {
    new CameraToGlobalX();
  }
}
