package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraResetZoom {
  public CameraResetZoom() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setZoom(3);
    myStage.waitSeconds(2);
    // Back to the stage at its own size.
    myCamera.resetZoom();
  }

  public static void main(String[] args) {
    new CameraResetZoom();
  }
}
