package demos.catPlatformer;

import java.util.ArrayList;
import java.util.List;

import org.openpatch.scratch.Stage;
import org.openpatch.scratch.Text;

/**
 * The Scratch for Java cat in a small platformer: collect the coffee beans,
 * jump on the bugs (or punch them) and climb up to the steaming mug.
 *
 * <p>
 * Arrow keys walk, climb and crouch, space jumps, X punches. Like the coin
 * collector, it only uses assets built into Scratch for Java - the cat, the
 * beans, the bugs, the mug and the hearts are on the built-in cat sheet, the
 * tiles and the sounds are Kenney's.
 */
public class CatPlatformer extends Stage {

  /** The height the ground reaches up to. */
  public static final double GROUND_TOP = -176;

  /** Where the ladder ends: the top of the platform with the mug. */
  public static final double LADDER_TOP = 112;

  private final List<Platform> platforms = new ArrayList<>();
  private final Heart[] hearts = new Heart[3];

  private Text score;
  private int beans = 0;
  private int totalBeans = 0;

  public CatPlatformer() {
    super(800, 480);
    this.addBackdrop("background");

    // The ground, one grass tile at a time.
    for (int x = -400; x < 400; x += 64) {
      this.add(new Ground(x + 32));
    }

    // A ladder from the ground up to the highest platform, added before the
    // platform so it is drawn behind it.
    for (double y = GROUND_TOP + 32; y < LADDER_TOP - 32; y += 64) {
      this.add(new Ladder(232, y, false));
    }
    this.add(new Ladder(232, LADDER_TOP - 32, true));

    // Three platforms, each from its left edge, its top and a number of tiles.
    this.addPlatform(-260, -80, 2);
    this.addPlatform(-40, 16, 2);
    this.addPlatform(200, LADDER_TOP, 3);

    this.addBean(-330, GROUND_TOP + 40);
    this.addBean(-60, GROUND_TOP + 40);
    this.addBean(-196, -80 + 40);
    this.addBean(24, 16 + 40);
    this.addBean(232, -40);
    this.addBean(300, LADDER_TOP + 40);

    this.add(new Bug(-170, -20, GROUND_TOP));
    this.add(new Bug(40, 160, GROUND_TOP));
    this.add(new Bug(280, 340, LADDER_TOP));

    this.add(new Goal(372, LADDER_TOP));
    this.add(new Cat());

    for (int i = 0; i < this.hearts.length; i++) {
      this.hearts[i] = new Heart(-370 + i * 34, 210);
      this.add(this.hearts[i]);
    }
    this.score = new Text();
    this.score.setPosition(-180, 210);
    this.score.setTextSize(24);
    this.add(this.score);
    this.showScore();
  }

  private void addPlatform(double left, double top, int tiles) {
    for (int i = 0; i < tiles; i++) {
      String costume = i == 0 ? "grassHalf_left" : i == tiles - 1 ? "grassHalf_right" : "grassHalf_mid";
      Platform tile = new Platform(costume, left + 32 + i * 64, top);
      this.platforms.add(tile);
      this.add(tile);
    }
  }

  private void addBean(double x, double y) {
    this.add(new Bean(x, y));
    this.totalBeans += 1;
  }

  private void showScore() {
    this.score.showText("Beans: " + this.beans + " / " + this.totalBeans);
  }

  /**
   * Finds what a falling sprite lands on. Between two frames its feet went
   * from feetBefore down to feetAfter; if a platform top (or the ground) lies
   * in between, below x, that is where it stands.
   *
   * @return the height of what it lands on, or NaN while it is still falling
   */
  public double landingHeight(double x, double feetBefore, double feetAfter) {
    double best = Double.NaN;
    for (Platform platform : this.platforms) {
      boolean above = x > platform.getLeft() && x < platform.getRight();
      double top = platform.getTop();
      if (above && feetBefore >= top && feetAfter <= top && !(best >= top)) {
        best = top;
      }
    }
    if (Double.isNaN(best) && feetAfter <= GROUND_TOP) {
      best = GROUND_TOP;
    }
    return best;
  }

  /** Called by a bean when the cat picks it up. */
  public void collectBean() {
    this.beans += 1;
    this.showScore();
  }

  /** Called by the cat when it gets hurt; returns how many hearts are left. */
  public int loseHeart() {
    int left = 0;
    for (Heart heart : this.hearts) {
      if (heart.isFull()) {
        left += 1;
      }
    }
    if (left > 0) {
      this.hearts[left - 1].empty();
      left -= 1;
    }
    return left;
  }

  public void win() {
    this.playSound("jingles_NES00");
    this.showMessage("You made it! " + this.beans + " / " + this.totalBeans + " beans");
  }

  public void lose() {
    this.showMessage("Game over");
  }

  private void showMessage(String message) {
    Text text = new Text();
    text.setPosition(0, 60);
    text.setTextSize(40);
    text.showText(message);
    this.add(text);
  }

  public static void main(String[] args) {
    new CatPlatformer();
  }
}
