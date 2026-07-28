package reference;
import org.openpatch.scratch.*;


public class StageConstructors {
  public StageConstructors() {
    // A stage of a given size. Without one the stage fills the window.
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);
    System.out.println("The stage is " + myStage.getWidth() + " by " + myStage.getHeight() + ".");
  }

  public static void main(String[] args) {
    new StageConstructors();
  }
}
