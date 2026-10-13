import org.openpatch.scratch.*;

public class GameStage extends Stage {

  // begin stage designer
  private Alien alien;
  // end stage designer

  private Text scoreText = new Text();
  private int dodged = 0;

  public GameStage() {

    this.scoreText.setPosition(0, 165);
    this.scoreText.setTextSize(20);
    this.add(this.scoreText);
    this.showScore();

    // begin stage designer
    this.addBackdrop("background");
    alien = new Alien();
    this.add(alien);
    // end stage designer
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
