import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.text.Text;

public class GameOverStage extends Stage {
  public GameOverStage() {

    this.add(new Background());

    var text = new Text();
    text.setTextSize(48);
    text.setTextColor(200, 100, 100);
    text.showText("Level " + Game.LEVEL + " is too hard for you!");
    text.setPosition(0, 0);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.showText("Press Space to start again!");
    text.setPosition(0, -40);
    this.add(text);
  }

  public void whenKeyPressed(int keyCode) {
    System.out.println(keyCode);
    if (keyCode == KeyCode.VK_SPACE) {
      Game.LEVEL = 0;
      Window.getInstance().setStage(new WorldStage());
    }
  }
}
