package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraSetZoom {
  public CameraSetZoom() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // 1 is the stage at its own size, 2 is twice as close.
    while (true) {
      myCamera.setZoom(1);
      myStage.wait(1200);
      myCamera.setZoom(2);
      myStage.wait(1200);
    }
  }

  public static void main(String[] args) {
    new CameraSetZoom();
  }
}
