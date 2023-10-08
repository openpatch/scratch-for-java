import org.openpatch.scratch.*;

public class SpriteRemoveSound {

  public SpriteRemoveSound() {
    new Stage(254, 100);
    Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
    zeta.addSound("bump", "assets/bump.wav");
    zeta.removeSound("bump");
    Window.getInstance().exit();
  }

  public static void main(String[] args) {
    new SpriteRemoveSound();
  }
}
