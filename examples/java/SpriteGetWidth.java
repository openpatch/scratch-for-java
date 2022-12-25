import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteGetWidth {
    public SpriteGetWidth() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        mySprite.say("Width: " + mySprite.getWidth());
    }

    public static void main(String[] args) {
        new SpriteGetWidth();
    }
}