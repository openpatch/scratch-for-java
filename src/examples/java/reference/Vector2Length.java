package reference;
import org.openpatch.scratch.*;


public class Vector2Length {
  public Vector2Length() {
    Vector2 myVector = new Vector2(60, 80);
    System.out.println("The vector " + myVector + " is " + myVector.length() + " long.");
  }

  public static void main(String[] args) {
    new Vector2Length();
  }
}
