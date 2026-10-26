package reference;
import org.openpatch.scratch.*;


public class RandomNoiseSeed {
  public RandomNoiseSeed() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);

    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(3);
    // The same seed gives the same hills every time the program runs.
    Random.noiseSeed(2000);
    for (int x = -300; x < 300; x++) {
      myPen.setPosition(x, Random.noise(x / 100.0) * 200 - 100);
      myPen.down();
    }
    myPen.up();
  }

  public static void main(String[] args) {
    new RandomNoiseSeed();
  }
}
