package Pipes;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.math.*;

public class Pipes extends Stage {
  public Pipes() {
    super(1280, 800);
    this.addBackdrop("chalkBoard", "Pipes/backdrops/chalk_board.jpg");
    this.setTint(60);
    this.add(new PenSprite());
    this.addSound("bg", "Pipes/sounds/bensound-enigmatic.wav");
  }

  public void whenKeyPressed(int keyCode) {
    if (keyCode == KeyCode.VK_SPACE) {
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
