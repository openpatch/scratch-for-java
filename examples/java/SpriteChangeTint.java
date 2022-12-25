import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteChangeTint {
    public SpriteChangeTint() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        myStage.wait(3000);
        mySprite.changeTint(150);
    }

    public static void main(String[] args) {
        new SpriteChangeTint();
    }
}
