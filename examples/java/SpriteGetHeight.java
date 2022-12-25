import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteGetHeight {
    public SpriteGetHeight() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeY(30);
        myStage.add(mySprite);
        mySprite.say("Height: " + mySprite.getHeight());
    }

    public static void main(String[] args) {
        new SpriteGetHeight();
    }
}