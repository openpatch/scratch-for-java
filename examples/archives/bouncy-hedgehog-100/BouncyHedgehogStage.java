import org.openpatch.scratch.Stage;

public class BouncyHedgehogStage extends Stage {
    
    public BouncyHedgehogStage() {
        this.addBackdrop("playground", "playground.jpg");
        this.add(new HedgehogSprite());
        this.add(new TrampolineSprite());
    }

    public static void main(String[] args) {
        new BouncyHedgehogStage();
    }
}
