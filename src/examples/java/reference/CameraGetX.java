package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.camera.Camera;


public class CameraGetX {
  public CameraGetX() {

    Stage myStage = new Stage(600, 240);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setX(-150);
    System.out.println("The camera looks at x = " + myCamera.getX() + ".");
  }

  public static void main(String[] args) {
    new CameraGetX();
  }
}
