package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraConstructors {
  public CameraConstructors() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    // Every stage has a camera of its own, so one is rarely built by hand. It
    // decides which part of the stage is on screen and how close.
    myCamera.setPosition(100, 0);
    myCamera.setZoom(1.5);
  }

  public static void main(String[] args) {
    new CameraConstructors();
  }
}
