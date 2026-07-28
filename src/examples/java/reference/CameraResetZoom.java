package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraResetZoom {
  public CameraResetZoom() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setZoom(3);
    myStage.wait(2000);
    // Back to the stage at its own size.
    myCamera.resetZoom();
  }

  public static void main(String[] args) {
    new CameraResetZoom();
  }
}
