package reference;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.sound.SoundPlayback;

public class SoundPlaybackExample {
  public SoundPlaybackExample() {
    Window window = new Window(400, 240);
    Stage stage = new Stage() {
      @Override public void whenKeyPressed(KeyCode key) {
        if (key == KeyCode.SPACE) {
          SoundPlayback quiet = this.playSoundCopy("hit", 20);
          SoundPlayback loud = this.playSoundCopy("hit", 80);
          quiet.setVolume(30);
          // quiet.stop() stops only this play; loud continues.
        }
        if (key == KeyCode.ESCAPE) this.stopAllSounds();
      }
    };
    stage.addSound("hit", "impactWood_heavy_001");
    stage.display("Space: overlapping sounds. Escape: stop them.");
    window.setStage(stage);
  }
  public static void main(String[] args) { new SoundPlaybackExample(); }
}
