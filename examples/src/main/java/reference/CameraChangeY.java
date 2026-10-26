package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraChangeY {
  public CameraChangeY() {

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
      while (myCamera.getY() < 100) {
        myCamera.changeY(2);
        myStage.waitSeconds(0.03);
      }
      while (myCamera.getY() > -100) {
        myCamera.changeY(-2);
        myStage.waitSeconds(0.03);
      }
    }
  }

  public static void main(String[] args) {
    new CameraChangeY();
  }
}
