import org.openpatch.scratch.*;

public class GameStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Alien alien;
  // scratch4j:end fields

  private Text scoreText = new Text();
  private int dodged = 0;

  public GameStage() {

    this.scoreText.setPosition(0, 165);
    this.scoreText.setTextSize(20);
    this.add(this.scoreText);
    this.showScore();

    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("background");
    alien = new Alien();
    this.add(alien);
    // scratch4j:end setup
    for (int i = 0; i < 3; i++) {
      this.add(new Rock());
    }
  }

  public void addDodge() {
    this.dodged = this.dodged + 1;
    this.showScore();
  }

  public void gameOver() {
    Window.getInstance().setStage(new TitleStage("You dodged " + this.dodged));
  }

  private void showScore() {
    this.scoreText.showText("Dodged: " + this.dodged);
  }
}
