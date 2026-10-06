package demos.donutIO;

import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Window;
import org.openpatch.scratch.Text;

public class WinStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  // scratch4j:end fields

  public WinStage() {
    var bg = new Background();
    bg.setTransparency(50);
    this.add(bg);

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

    // scratch4j:begin setup (managed by the stage designer)
    // scratch4j:end setup
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      Game.LEVEL += 1;
      Window.getInstance().transitionToStage(new WorldStage(), 500);
    }
  }
}
