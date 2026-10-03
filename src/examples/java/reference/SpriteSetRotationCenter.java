package reference;
import org.openpatch.scratch.*;

public class SpriteSetRotationCenter {
  public SpriteSetRotationCenter() {
    Stage myStage = new Stage(600, 240);

    // turns around the middle of its costume
    Sprite middle = new Sprite("rock", "tappy_plane/rock");
    middle.setSize(40);
    middle.setPosition(-150, 0);
    myStage.add(middle);

    // turns around the middle of its bottom edge, like a clock hand
    Sprite hand = new Sprite("rock", "tappy_plane/rock");
    hand.setRotationCenter(54, 239);
    hand.setSize(40);
    hand.setPosition(150, 0);
    myStage.add(hand);

    while (true) {
      middle.turnRight(2);
      hand.turnRight(2);
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new SpriteSetRotationCenter();
  }
}
