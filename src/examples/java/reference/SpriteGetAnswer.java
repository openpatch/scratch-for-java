package reference;
import org.openpatch.scratch.*;


public class SpriteGetAnswer {
  public SpriteGetAnswer() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("slime", "slimeGreen");
    myStage.add(mySprite);

    mySprite.ask("How old are you?");
    while (mySprite.isAsking()) {
      myStage.wait(50);
    }
    System.out.println("The answer was " + mySprite.getAnswer());
  }

  public static void main(String[] args) {
    new SpriteGetAnswer();
  }
}
