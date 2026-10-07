import org.openpatch.scratch.*;

/** Shared logic: desktop JUnit and the browser runner invoke this unchanged. */
public class PortableBehaviorChecks {
  public static void run() {
    CheckWalker player = new CheckWalker();
    player.setPosition(0, 0);
    for (int frame = 0; frame < 10; frame++) player.run();
    require(player.getX() == 30, "ten movement frames");
    Sprite coin = new Sprite("slime", "slimeGreen");
    coin.setPosition(30, 0);
    require(player.getHitbox().intersects(coin.getHitbox()), "collision");
    if (player.getHitbox().intersects(coin.getHitbox())) player.score++;
    require(player.score == 8, "score after collecting");
    coin.setX(1000);
    require(!player.getHitbox().intersects(coin.getHitbox()), "separated sprites");
    CheckWalker copy = (CheckWalker) player.clone();
    require(copy.score == 8 && copy.isClone(), "clone fields");
    copy.score = 9;
    require(player.score == 8, "independent score");
    require(player.getTimer("check") != copy.getTimer("check"), "independent timers");
    Random.randomSeed(2026);
    int first = Random.randomInt(100);
    int second = Random.randomInt(100);
    Random.randomSeed(2026);
    require(first == Random.randomInt(100) && second == Random.randomInt(100), "seeded randomness");
    require(first >= 0 && first <= 100, "random range");
  }
  private static void require(boolean value, String message) {
    if (!value) throw new RuntimeException(message);
  }
}

class CheckWalker extends Sprite {
  int score = 7;
  CheckWalker() { super("slime", "slimeGreen"); }
  public void run() { changeX(3); }
}
