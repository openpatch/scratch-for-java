import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsMouseDown {
    public SpriteIsMouseDown() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeX(-80);
        mySprite.changeY(30);
        myStage.add(mySprite);

        while(true) {
            mySprite.say("Mouse down? " + mySprite.isMouseDown());
        }
    }

    public static void main(String[] args) {
        new SpriteIsMouseDown();
    }
}
