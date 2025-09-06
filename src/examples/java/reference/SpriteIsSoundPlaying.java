package reference;
import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.*;

public class SpriteIsSoundPlaying {
  public SpriteIsSoundPlaying() {
    Stage myStage = new Stage(600, 240);
    Sprite mySprite = new Sprite("zeta", "assets/zeta_green_badge.png");
    mySprite.addSound("bump", "assets/bump.wav");
    myStage.add(mySprite);

    GifRecorder recorder =
        new GifRecorder("examples/reference/" + this.getClass().getName() + ".gif");
    recorder.start();
    
    // Check if sound is playing before starting
    mySprite.say("Playing: " + mySprite.isSoundPlaying("bump"));
    myStage.wait(1000);
    
    // Play sound and check status
    mySprite.playSound("bump");
    mySprite.say("Playing: " + mySprite.isSoundPlaying("bump"));
    myStage.wait(2000);
    
    recorder.stop();
    myStage.exit();
  }

  public static void main(String[] args) {
    new SpriteIsSoundPlaying();
  }
}