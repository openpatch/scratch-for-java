import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class SpriteSetRotation {
    public SpriteSetRotation() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("zeta", "examples/java/assets/zeta_green_badge.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        mySprite.say("Rotation: " + mySprite.getRotation());
        myStage.wait(3000);
        mySprite.setRotation(45);
        mySprite.say("Rotation: " + mySprite.getRotation());
    }

    public static void main(String[] args) {
        new SpriteSetRotation();
    }
}
