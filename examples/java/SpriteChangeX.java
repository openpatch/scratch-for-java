import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteChangeX {
    public SpriteChangeX() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        myStage.wait(3000);
        mySprite.changeX(100);
    }

    public static void main(String[] args) {
        new SpriteChangeX();
    }
}