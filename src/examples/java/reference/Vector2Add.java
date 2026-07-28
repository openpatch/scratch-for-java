package reference;
import org.openpatch.scratch.*;


public class Vector2Add {
  public Vector2Add() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);
    myPen.setSize(4);

    Vector2 right = new Vector2(120, 0);
    Vector2 up = new Vector2(0, 80);
    Vector2 sum = right.add(up);

    // red: the first vector
    myPen.setColor(0);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(right.getX(), right.getY());
    myPen.up();
    // green: the second one
    myPen.setColor(85);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(up.getX(), up.getY());
    myPen.up();
    // blue: the two added together
    myPen.setColor(170);
    myPen.setPosition(0, 0);
    myPen.down();
    myPen.setPosition(sum.getX(), sum.getY());
    myPen.up();
    System.out.println(right + " + " + up + " = " + sum);
  }

  public static void main(String[] args) {
    new Vector2Add();
  }
}
