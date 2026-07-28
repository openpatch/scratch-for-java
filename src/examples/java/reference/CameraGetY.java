package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraGetY {
  public CameraGetY() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setY(80);
    System.out.println("The camera looks at y = " + myCamera.getY() + ".");
  }

  public static void main(String[] args) {
    new CameraGetY();
  }
}
