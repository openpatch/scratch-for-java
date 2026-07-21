package demos.volume;

import org.openpatch.scratch.*;

/**
 * Turning sounds up and down with setVolume() and changeVolume().
 *
 * <p>
 * Press space to play a sound, the up and down arrows to change how loud it is.
 * The sound is one of the ones built into Scratch for Java, so there is no file
 * to find.
 */
public class Volume extends Stage {

  private final Text label = new Text();

  public Volume() {
    super(480, 260);
    this.addBackdrop("background");

    this.addSound("handleCoins");
    this.setVolume(100);

    this.label.setPosition(0, 40);
    this.label.setTextSize(20);
    this.add(this.label);

    Sprite speaker = new Sprite();
    speaker.addCostume("hudCoin");
    speaker.setSize(60);
    speaker.setY(-40);
    this.add(speaker);

    this.showVolume();
  }

  private void showVolume() {
    this.label.showText("Volume: " + Math.round(this.getVolume())
        + "%   (space = play, up/down = louder/quieter)");
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      this.playSound("handleCoins");
    }
    if (keyCode == KeyCode.UP) {
      this.changeVolume(10);
      this.showVolume();
    }
    if (keyCode == KeyCode.DOWN) {
      this.changeVolume(-10);
      this.showVolume();
    }
  }

  public static void main(String[] args) {
    new Volume();
  }
}
