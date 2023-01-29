import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extensions.GifRecorder;

public class StageWhenBackdropSwitches {
    class CustomStage extends Stage {
        public CustomStage() {
            super(254, 100);
            this.addBackdrop("forest", "assets/background_forest.png");
            this.addBackdrop("sea", "assets/background_sea.png");
        }

        public void whenBackdropSwitches(String name) {
            if (name.equals("sea")) {
                this.display("Sea");
            } else if (name.equals("forest")) {
                this.display("Team Trees!");
            }
        }
    }

    public StageWhenBackdropSwitches() {
        Stage myStage = new CustomStage();
        GifRecorder recorder = new GifRecorder("" + this.getClass().getName() + ".gif");
        recorder.start();
        myStage.wait(1000);
        myStage.nextBackdrop();
        myStage.wait(1000);
        myStage.nextBackdrop();
        myStage.wait(1000);
        recorder.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        new StageWhenBackdropSwitches();
    }
}