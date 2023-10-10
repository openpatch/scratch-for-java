import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteBroadcast {

  public SpriteBroadcast() {
    Stage myStage = new ReceiveStage();
    myStage.add(new BroadcastSprite());
    myStage.add(new ReceiveSprite());
    GifRecorder recorder = new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    while (myStage.getTimer().forMillis(3000)) {}
    recorder.stop();
    myStage.exit();
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
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    this.addCostume("gamma", "assets/gamma_purple_badge.png");
  }

  public void whenClicked() {
    this.broadcast("change-custome");
    this.nextCostume();
  }
}

class ReceiveSprite extends Sprite {

  public ReceiveSprite() {
    this.addCostume("zeta", "assets/zeta_green_badge.png");
    this.addCostume("gamma", "assets/gamma_purple_badge.png");
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
