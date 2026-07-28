package reference;
import org.openpatch.scratch.*;

public class SpriteBroadcast {
  public SpriteBroadcast() {
    Stage myStage = new ReceiveStage();
    myStage.add(new BroadcastSprite());
    myStage.add(new ReceiveSprite());
    while (true) {}
  }

  public static void main(String[] args) {
    new SpriteBroadcast();
  }
}

class ReceiveStage extends Stage {
  public ReceiveStage() {
    super(600, 240);
  }

  public void whenIReceive(String message) {
    if (message.equals("change-custome")) {
      this.changeColor(50);
    }
  }
}

class BroadcastSprite extends Sprite {
  public BroadcastSprite() {
    this.addCostume("zeta", "slimeGreen");
    this.addCostume("gamma", "slimePurple");
  }

  public void whenClicked() {
    this.broadcast("change-custome");
    this.nextCostume();
  }
}

class ReceiveSprite extends Sprite {
  public ReceiveSprite() {
    this.addCostume("zeta", "slimeGreen");
    this.addCostume("gamma", "slimePurple");
    this.changeX(50);
  }

  @Override
  public void whenIReceive(String message) {
    if ("change-custome".equals(message)) {
      this.nextCostume();
      this.say("Got it!");
    }
  }
}
