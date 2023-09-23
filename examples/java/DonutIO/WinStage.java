import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.text.Text;

public class WinStage extends Stage {
  public WinStage() {
    this.add(new Background());

    var text = new Text();
    text.setTextSize(48);
    text.setTextColor(200, 100, 100);
    text.showText("Level complete!");
    text.setPosition(0, 0);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.showText("Press Space for the next level!");
    text.setPosition(0, -40);
    this.add(text);
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_SPACE) {
      Game.LEVEL += 1;
      Window.getInstance().setStage(new WorldStage());
    }
  }
}
