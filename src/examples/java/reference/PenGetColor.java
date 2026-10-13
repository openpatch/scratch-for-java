package reference;
import org.openpatch.scratch.*;


public class PenGetColor {
  public PenGetColor() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
