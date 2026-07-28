package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraToLocalY {
  public CameraToLocalY() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setPosition(0, 60);
    System.out.println("y = 60 on the stage is drawn at y = " + myCamera.toLocalY(60));
  }

  public static void main(String[] args) {
    new CameraToLocalY();
  }
}
