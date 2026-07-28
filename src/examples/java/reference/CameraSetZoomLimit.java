package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraSetZoomLimit {
  public CameraSetZoomLimit() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // However far the zoom is turned, it stays between these two.
    myCamera.setZoomLimit(0.5, 1.5);
    while (true) {
      myCamera.changeZoom(0.02);
      myStage.display("zoom: " + myCamera.getZoom());
      myStage.wait(50);
    }
  }

  public static void main(String[] args) {
    new CameraSetZoomLimit();
  }
}
