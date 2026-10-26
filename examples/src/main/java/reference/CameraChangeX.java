package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraChangeX {
  public CameraChangeX() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
        myStage.waitSeconds(0.03);
      }
      while (myCamera.getX() > -200) {
        myCamera.changeX(-2);
        myStage.waitSeconds(0.03);
      }
    }
  }

  public static void main(String[] args) {
    new CameraChangeX();
  }
}
