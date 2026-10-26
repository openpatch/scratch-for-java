package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.Camera;


public class CameraGetZoom {
  public CameraGetZoom() {

    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
    for (int i = 0; i < 12; i++) {
      Sprite coin = new Sprite("coin", "coinGold");
      coin.goToRandomPosition();
      myStage.add(coin);
    }
    Camera myCamera = myStage.getCamera();

    myCamera.setZoom(1.5);
    System.out.println("The camera is at " + myCamera.getZoom() + " times its own size.");
  }

  public static void main(String[] args) {
    new CameraGetZoom();
  }
}
