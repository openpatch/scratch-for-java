package reference;
import org.openpatch.scratch.*;


public class StageBroadcast {
  public StageBroadcast() {
    class MySprite extends Sprite {
      public MySprite() {
        this.addCostume("slime", "slimeGreen");
      }

      public void whenIReceive(String message) {
        if (message.equals("up")) {
          this.setY(50);
        } else if (message.equals("down")) {
          this.setY(-50);
        }
      }
    }

    Stage myStage = new Stage(600, 240);
    myStage.add(new MySprite());

    // Every sprite on the stage hears the message.
    while (true) {
      myStage.broadcast("up");
      myStage.wait(600);
      myStage.broadcast("down");
      myStage.wait(600);
    }
  }

  public static void main(String[] args) {
    new StageBroadcast();
  }
}
