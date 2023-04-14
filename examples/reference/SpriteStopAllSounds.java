import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.*;

public class SpriteStopAllSounds {

    public SpriteStopAllSounds() {
        Stage myStage = new Stage(254, 100);
        Sprite zeta = new Sprite("green", "assets/zeta_green_badge.png");
        zeta.addSound("bump", "assets/bump.wav");
        zeta.addSound("music", "assets/music.mp3");
        zeta.playSound("bump");
        zeta.playSound("music");
        myStage.wait(300);
        zeta.stopAllSounds();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteStopAllSounds();
    }
}
