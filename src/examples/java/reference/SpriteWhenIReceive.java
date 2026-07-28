package reference;
import org.openpatch.scratch.*;


public class SpriteWhenIReceive {
  public SpriteWhenIReceive() {
    class MySprite extends Sprite {
      public MySprite() {
        this.addCostume("slime", "slimeGreen");
      }

      // Every sprite on the stage hears every message.
      public void whenIReceive(String message) {
        if (message.equals("up")) {
          this.setY(50);
        } else if (message.equals("down")) {
          this.setY(-50);
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    MySprite mySprite = new MySprite();
    myStage.add(mySprite);

    while (true) {
      myStage.broadcast("up");
      myStage.wait(600);
      myStage.broadcast("down");
      myStage.wait(600);
    }
  }

  public static void main(String[] args) {
    new SpriteWhenIReceive();
  }
}
