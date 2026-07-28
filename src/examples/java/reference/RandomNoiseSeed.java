package reference;
import org.openpatch.scratch.*;


public class RandomNoiseSeed {
  public RandomNoiseSeed() {
    Stage myStage = new Stage(600, 240);

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
