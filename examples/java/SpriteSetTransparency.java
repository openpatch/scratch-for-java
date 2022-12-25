import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetTransparency {
    public SpriteSetTransparency() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "examples/java/assets/slime.png");
        myStage.add(mySprite);
        myStage.wait(2000);
        mySprite.setTransparency(50);
    }

    public static void main(String[] args) {
        new SpriteSetTransparency();
    }
}
