import org.openpatch.scratch.*;

public class CatchStage extends Stage {
  private Text scoreText = new Text();
  private int score = 0;

  public CatchStage() {
    super(600, 400);
    this.addBackdrop("background");
    this.addSound("handleCoins");

    this.scoreText.setPosition(-270, 170);
    this.scoreText.setTextSize(22);
    this.add(this.scoreText);
    this.showScore();

    this.add(new Basket());
    for (int i = 0; i < 4; i++) {
      this.add(new Coin());
    }
  }

  public void addPoint() {
    this.score = this.score + 1;
    this.playSound("handleCoins");
    this.showScore();
  }

  private void showScore() {
    this.scoreText.showText("Coins: " + this.score);
  }

  public static void main(String[] args) {
    new CatchStage();
  }
}
