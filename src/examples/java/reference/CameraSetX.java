package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraSetX {
  public CameraSetX() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    while (true) {
      myCamera.setX(-150);
      myStage.wait(1000);
      myCamera.setX(150);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new CameraSetX();
  }
}
