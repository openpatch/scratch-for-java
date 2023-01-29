import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class SpriteIsVisible {
    public SpriteIsVisible() {
        Stage myStage = new Stage(254, 100);
        Sprite gamma = new Sprite("gamma", "assets/gamma_purple_badge.png");
        myStage.add(gamma);
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.display("Gamma Visible? " + gamma.isVisible());
        myStage.wait(2000);
        gamma.hide();
        myStage.display("Gamma Visible? " + gamma.isVisible());
        myStage.wait(2000);
        recorder.stop();
        System.exit(0);
    }    

    public static void main(String[] args) {
        new SpriteIsVisible();
    }
}