import org.openpatch.scratch.*;

public class TitleStage extends Stage {
  public TitleStage(String message) {
    this.addBackdrop("background");

    Text title = new Text();
    title.setPosition(0, 60);
    title.setTextSize(30);
    title.showText(message);
    this.add(title);

    Text hint = new Text();
    hint.setPosition(0, 10);
    hint.setTextSize(18);
    hint.showText("Press SPACE to play");
    this.add(hint);
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      Window.getInstance().setStage(new GameStage());
    }
  }
}
