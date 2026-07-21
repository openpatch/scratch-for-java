package demos.glide;

import org.openpatch.scratch.*;

/**
 * Sprites sliding to a new place with glide(), instead of jumping there.
 *
 * <p>
 * Click anywhere and the alien glides to the mouse over one second. The coin
 * glides between two corners for as long as the program runs.
 */
public class Glide extends Stage {

  public Glide() {
    super(600, 400);
    this.addBackdrop("background");
    this.add(new Walker());
    this.add(new Patroller());
  }

  public static void main(String[] args) {
    new Glide();
  }
}

/** Glides to wherever the mouse was clicked. */
class Walker extends Sprite {
  public Walker() {
    this.addCostume("alienGreen_stand");
    this.setSize(35);
  }

  public void whenClicked() {
    // whenClicked fires on the sprite; glide to the middle from wherever it is
    this.glide(1, 0, 0);
  }

  public void run() {
    if (this.isMouseDown() && !this.isGliding()) {
      this.glide(1, this.getMouseX(), this.getMouseY());
    }
  }
}

/** Glides back and forth between two corners, for ever. */
class Patroller extends Sprite {
  private boolean toTheRight = true;

  public Patroller() {
    this.addCostume("coinGold");
    this.setSize(50);
    this.setPosition(-220, 140);
  }

  public void run() {
    if (this.isGliding()) {
      return;
    }
    if (this.toTheRight) {
      this.glide(2, 220, 140);
    } else {
      this.glide(2, -220, 140);
    }
    this.toTheRight = !this.toTheRight;
  }
}
