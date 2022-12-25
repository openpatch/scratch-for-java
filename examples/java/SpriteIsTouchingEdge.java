import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteIsTouchingEdge {
    public SpriteIsTouchingEdge() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        mySprite.changeX(-80);
        myStage.add(mySprite);

        while(true) {
            mySprite.say("Is touching edge? " + mySprite.isTouchingEdge());
            mySprite.changeY(10);
            myStage.wait(500);
        }
    }

    public static void main(String[] args) {
        new SpriteIsTouchingEdge();
    }
}
