package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraSetZoom {
  public CameraSetZoom() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // 1 is the stage at its own size, 2 is twice as close.
    while (true) {
      myCamera.setZoom(1);
      myStage.waitSeconds(1.2);
      myCamera.setZoom(2);
      myStage.waitSeconds(1.2);
    }
  }

  public static void main(String[] args) {
    new CameraSetZoom();
  }
}
