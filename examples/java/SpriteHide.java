import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteHide {
    public SpriteHide() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        myStage.wait(3000);
        mySprite.hide();
    }
    public static void main(String[] args) {
        new SpriteHide();
    }
}
