import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteChangeY {
    public SpriteChangeY() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        myStage.wait(2000);
        mySprite.changeY(-20);
        myStage.wait(2000);
        mySprite.changeY(40);
    }

    public static void main(String[] args) {
        new SpriteChangeY();
    }
}
