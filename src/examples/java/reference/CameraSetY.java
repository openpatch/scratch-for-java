package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraSetY {
  public CameraSetY() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    while (true) {
      myCamera.setY(-80);
      myStage.wait(1000);
      myCamera.setY(80);
      myStage.wait(1000);
    }
  }

  public static void main(String[] args) {
    new CameraSetY();
  }
}
