package reference;
import org.openpatch.scratch.*;


public class ColorToString {
  public ColorToString() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    // Printing a colour tells you which one it is - the first thing worth
    // trying when a tint does not look the way it should.
    mySprite.setTint(255, 128, 0);
    System.out.println(mySprite.getTint());

    Color orange = new Color(255, 128, 0);
    System.out.println("Same colour? " + orange.equals(mySprite.getTint()));
  }

  public static void main(String[] args) {
    new ColorToString();
  }
}
