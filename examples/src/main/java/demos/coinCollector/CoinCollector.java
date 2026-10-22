package demos.coinCollector;

import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Text;
import org.openpatch.scratch.TextAlign;
import org.openpatch.scratch.Window;

/**
 * A small game that only uses assets built into Scratch for Java.
 *
 * <p>
 * There is no images or sounds folder next to these files. Every costume,
 * backdrop and sound is addressed by its name, for example
 * {@code addCostume("coinGold")}. All available names are listed at
 * https://scratch4j.openpatch.org/sprites and
 * https://scratch4j.openpatch.org/sounds
 */
public class CoinCollector extends Stage {

  // begin stage designer
  private Player player;
  // end stage designer

  /** The height the ground reaches up to. Sprites stand on this line. */
  public static final double GROUND_TOP = -176;

  private static final int COINS = 6;

  private Text score;
  private int collected = 0;

  public CoinCollector() {

    // A backdrop and a sound, by name - no files needed.

    // A row of grass tiles along the bottom.
    for (int x = -400; x < 400; x += 64) {
      this.add(new Ground(x + 32));
    }

    // A few coins to collect, spread across the level. They start to the right
    // of the player, so none of them is picked up right away.
    for (int i = 0; i < COINS; i++) {
      this.add(new Coin(-240 + i * 112, GROUND_TOP + 45));
    }

    // begin stage designer
    this.addBackdrop("background");
    this.addSound("jingles_NES00");
    player = new Player();
    this.add(player);
    // end stage designer

    this.score = new Text();
    this.score.setPosition(-380, 200);
    this.score.setAlign(TextAlign.LEFT);
    this.score.setTextSize(28);
    this.score.showText("Coins: 0 / " + COINS);
    this.add(this.score);
  }

  /** Called by a coin when the player picks it up. */
  public void collect() {
    this.collected += 1;
    this.score.showText("Coins: " + this.collected + " / " + COINS);

    if (this.collected == COINS) {
      this.playSound("jingles_NES00");

      Text won = new Text();
      won.setPosition(0, 60);
      won.setTextSize(48);
      won.showText("You got them all!");
      this.add(won);
    }
  }

  public static void main(String[] args) {
    Window myWindow = new Window(800, 480);
    myWindow.setStage(new CoinCollector());
  }
}
