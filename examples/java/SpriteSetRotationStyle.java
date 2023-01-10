import org.openpatch.scratch.RotationStyle;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteSetRotationStyle {
    public SpriteSetRotationStyle() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "assets/slime.png");
        myStage.add(mySprite);
        mySprite.changeX(-80);
        mySprite.changeY(30);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        mySprite.say("Rotation: " + mySprite.getRotation());
        myStage.wait(1000);
        mySprite.setRotation(45);
        mySprite.say("All-Around: " + mySprite.getRotation());
        myStage.wait(1000);
        mySprite.setRotationStyle(RotationStyle.DONT);
        mySprite.setRotation(180);
        mySprite.say("Don't: " + mySprite.getRotation());
        myStage.wait(1000);
        mySprite.setRotationStyle(RotationStyle.LEFT_RIGHT);
        mySprite.setRotation(200);
        mySprite.say("LEFT-RIGHT: " + mySprite.getRotation());
        myStage.wait(1000);
        recorder.stop();
        System.exit(0);

    }

    public static void main(String[] args) {
        new SpriteSetRotationStyle();
    }
}
