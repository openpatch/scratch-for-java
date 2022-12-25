import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetOnEdgeBounce {
    public SpriteSetOnEdgeBounce() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "examples/java/assets/slime.png");
        myStage.add(mySprite);
        mySprite.setOnEdgeBounce(true);
        mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);

        while(true) {
            mySprite.move(1);
            myStage.wait(20);
        }
    }

    public static void main(String[] args) {
        new SpriteSetOnEdgeBounce();
    }
}