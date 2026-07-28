package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraChangeY {
  public CameraChangeY() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    while (true) {
      while (myCamera.getY() < 100) {
        myCamera.changeY(2);
        myStage.wait(30);
      }
      while (myCamera.getY() > -100) {
        myCamera.changeY(-2);
        myStage.wait(30);
      }
    }
  }

  public static void main(String[] args) {
    new CameraChangeY();
  }
}
