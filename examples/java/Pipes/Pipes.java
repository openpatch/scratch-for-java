import org.openpatch.scratch.KeyCode;
import org.openpatch.scratch.Stage;

public class Pipes extends Stage {
    public Pipes() {
        super(1280, 800);
        this.addBackdrop("chalkBoard", "backdrops/chalk_board.jpg");
        this.setTint(60);
        this.add(new PenSprite());
        this.addSound("bg", "sounds/bensound-enigmatic.wav");
    }

    public void whenKeyPressed(int keyCode) {
        if (keyCode == KeyCode.VK_SPACE) {
            PenSprite.setColor((float) Math.random() * 255);
        }
    }

    public void run() {
        this.playSound("bg");
        var pens = this.find(PenSprite.class);
        for (var pen : pens) {
            if (Math.random() < 0.05 && pens.size() < 15) {
                this.add(new PenSprite((PenSprite) pen));
            }
            if (Math.random() < 0.01 && pens.size() > 1) {
                this.remove(pen);
            }
        }
    }

    public static void main(String[] args) {
        new Pipes();
    }

}
