package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraChangeZoom {
  public CameraChangeZoom() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // Zooming in a little at a time, then back out again.
    while (true) {
      while (myCamera.getZoom() < 2) {
        myCamera.changeZoom(0.01);
        myStage.wait(30);
      }
      while (myCamera.getZoom() > 1) {
        myCamera.changeZoom(-0.01);
        myStage.wait(30);
      }
    }
  }

  public static void main(String[] args) {
    new CameraChangeZoom();
  }
}
