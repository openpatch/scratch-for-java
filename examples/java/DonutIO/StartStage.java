import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.extensions.text.Text;

public class StartStage extends Stage {
  public StartStage() {

    this.add(new Background());

    var text = new Text();
    text.setTextSize(48);
    text.setTextColor(200, 100, 100);
    text.showText("Donut.io");
    text.setPosition(0, 100);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.setWidth(700);
    text.showText("Each level more donuts will hunt you! Only the biggest will survive.");
    text.setPosition(0, 60);
    this.add(text);

    text = new Text();
    text.setTextSize(32);
    text.setTextColor(200, 100, 100);
    text.showText("Press Space to start.");
    text.setPosition(0, -20);
    this.add(text);
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_SPACE) {
      Game.LEVEL = 0;
      Window.getInstance().setStage(new WorldStage());
    }
  }
}
