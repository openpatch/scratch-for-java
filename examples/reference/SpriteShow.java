import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteShow {
   public SpriteShow() {
        Stage myStage = new Stage(254, 100);
        Sprite mySprite = new Sprite("slime", "assets/slime.png");
        myStage.add(mySprite);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(3000);
        mySprite.hide();
        myStage.wait(3000);
        mySprite.show();
        myStage.wait(3000);
        recorder.stop();
        System.exit(0);
   } 

   public static void main(String[] args) {
    new SpriteShow();
   }
}
