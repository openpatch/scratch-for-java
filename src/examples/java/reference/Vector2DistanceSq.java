package reference;
import org.openpatch.scratch.*;


public class Vector2DistanceSq {
  public Vector2DistanceSq() {
    Vector2 here = new Vector2(-60, -40);
    Vector2 there = new Vector2(60, 40);
    // Comparing squared distances answers "which is nearer?" without a square root.
    System.out.println("distance " + here.distance(there) + ", squared " + here.distanceSq(there));
  }

  public static void main(String[] args) {
    new Vector2DistanceSq();
  }
}
