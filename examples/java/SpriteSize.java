import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSize {
    public SpriteSize() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        mySprite.say("Size: " + mySprite.getSize());
        myStage.wait(3000);
        mySprite.setSize(50);
        mySprite.say("Size: " + mySprite.getSize());
    }

    public static void main(String[] args) {
        new SpriteSize();
    }
}
