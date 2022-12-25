import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsKeyPressed {
    public SpriteIsKeyPressed() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeX(-80);
        mySprite.changeY(30);
        myStage.add(mySprite);

        while(true) {
            mySprite.say("Space pressed? " + mySprite.isKeyPressed(32));
        }
    }

    public static void main(String[] args) {
        new SpriteIsKeyPressed();
    }
}