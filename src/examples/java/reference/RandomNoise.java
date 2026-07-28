package reference;
import org.openpatch.scratch.*;


public class RandomNoise {
  public RandomNoise() {
    Stage myStage = new Stage(600, 240);

    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(3);
    // Noise wanders smoothly, unlike random(), which jumps. Drawing it left to
    // right gives a soft hilly line.
    for (int x = -300; x < 300; x++) {
      myPen.setPosition(x, Random.noise(x / 100.0) * 200 - 100);
      myPen.down();
    }
    myPen.up();
  }

  public static void main(String[] args) {
    new RandomNoise();
  }
}
