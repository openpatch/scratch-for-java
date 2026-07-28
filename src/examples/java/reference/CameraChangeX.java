package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraChangeX {
  public CameraChangeX() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // Scrolling sideways, which is how a platformer follows its player.
    while (true) {
      while (myCamera.getX() < 200) {
        myCamera.changeX(2);
        myStage.wait(30);
      }
      while (myCamera.getX() > -200) {
        myCamera.changeX(-2);
        myStage.wait(30);
      }
    }
  }

  public static void main(String[] args) {
    new CameraChangeX();
  }
}
