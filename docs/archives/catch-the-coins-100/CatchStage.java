import org.openpatch.scratch.*;

public class CatchStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private Basket basket;
  // scratch4j:end fields

  private Text scoreText = new Text();
  private int score = 0;

  public CatchStage() {
    super(600, 400);

    this.scoreText.setPosition(0, 170);
    this.scoreText.setTextSize(22);
    this.add(this.scoreText);
    this.showScore();

    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("background");
    this.addSound("handleCoins");
    basket = new Basket();
    this.add(basket);
    // scratch4j:end setup
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
