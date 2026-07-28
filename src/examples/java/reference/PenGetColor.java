package reference;
import org.openpatch.scratch.*;


public class PenGetColor {
  public PenGetColor() {
    Stage myStage = new Stage(600, 240);
    Pen myPen = new Pen();
    myStage.add(myPen);

    myPen.setColor(140);
    Color penColor = myPen.getColor();
    System.out.println("hue " + penColor.getH() + ", red " + penColor.getRed());
  }

  public static void main(String[] args) {
    new PenGetColor();
  }
}
