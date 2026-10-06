package demos.pipes;

import org.openpatch.scratch.*;
import org.openpatch.scratch.*;

public class Pipes extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  private PenSprite penSprite;
  // scratch4j:end fields

  public Pipes() {
    super(1280, 800);
    this.setTint(60);
    // scratch4j:begin setup (managed by the stage designer)
    this.addBackdrop("chalkBoard", "demos/pipes/backdrops/chalk_board.jpg");
    this.addSound("bg", "demos/pipes/sounds/bensound-enigmatic.ogg");
    penSprite = new PenSprite();
    this.add(penSprite);
    // scratch4j:end setup
  }

  public void whenKeyPressed(KeyCode keyCode) {
    if (keyCode == KeyCode.SPACE) {
      PenSprite.setColor(Random.random(255));
    }
  }

  public void run() {
    this.playSound("bg");
    var pens = this.find(PenSprite.class);
    for (var pen : pens) {
      if (Random.random() < 0.05 && pens.size() < 15) {
        this.add(new PenSprite((PenSprite) pen));
      }
      if (Random.random() < 0.01 && pens.size() > 1) {
        this.remove(pen);
      }
    }
  }

  public static void main(String[] args) {
    new Pipes();
  }
}
