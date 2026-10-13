package reference;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;

public class StageFind {
  public StageFind() {
    Window myWindow = new Window(600, 240);
    Stage myStage = new Stage();
    myWindow.setStage(myStage);
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
