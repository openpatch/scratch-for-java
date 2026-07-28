package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class StageFind {
  public StageFind() {
    Stage myStage = new Stage(600, 240);
    myStage.add(new CustomSprite());
    myStage.add(new CustomSprite());
    myStage.add(new Sprite());
    myStage.display("Sprites: " + myStage.find(CustomSprite.class).size());
    myStage.wait(2000);
  }

  class CustomSprite extends Sprite {}

  public static void main(String[] args) {
    new StageFind();
  }
}
