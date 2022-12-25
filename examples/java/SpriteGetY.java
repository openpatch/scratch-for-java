import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteGetY {
    public SpriteGetY() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        mySprite.say("Y: " + mySprite.getY());
    }

    public static void main(String[] args) {
        new SpriteGetY();
    }
}
