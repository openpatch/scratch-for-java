import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;
import org.openpatch.scratch.Window;

public class SpriteWhenClicked {

    class CustomSprite extends Sprite {
        public CustomSprite() {
            this.addCostume("zeta", "assets/zeta_green_badge.png");
            this.addCostume("gamma", "assets/gamma_purple_badge.png");
        }

        @Override
        public void whenClicked() {
            this.nextCostume();
        }
    }

    public SpriteWhenClicked() {
        Stage myStage = new Stage(254, 100);
        myStage.add(new CustomSprite());
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        while(myStage.getTimer().forMillis(3000));
        recorder.stop();
        Window.getInstance().exit();
    }

    public static void main(String[] args) {
        new SpriteWhenClicked();
    }
}