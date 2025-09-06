package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteStopSound {
  public SpriteStopSound() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.addSound("bump", "assets/bump.wav");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    mySprite.say("Playing sound...");
    mySprite.playSound("bump");
    myStage.wait(1000);
    
    mySprite.say("Stopping sound");
    mySprite.stopSound("bump");
    myStage.wait(2000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteStopSound();
  }
}