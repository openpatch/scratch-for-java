package parity;

import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.recorder.FrameRecorder;

/**
 * Every arrangement that has to keep looking the way it looks.
 *
 * <p>
 * {@link ParityProbe} covers what the library can be asked; this covers what it
 * draws. The two are answerable in different ways: a number can be compared,
 * where a picture can only be looked at - so the bugs that lived longest this
 * side were the ones nothing could see. The speech bubble hung off the corner of
 * the costume canvas rather than the sprite, with its tail pointing at nothing.
 * The stage's display line sat in the top left instead of along the bottom. A
 * text came out at 24pt where it should have been 14. Every one of those was
 * found by somebody noticing.
 *
 * <p>
 * Each scene below is set up, given a few frames to settle, and saved as a PNG.
 * {@code ./scripts/visual-parity.sh} renders them again and compares. Keep them
 * still: no timers, no randomness, nothing that moves, or the comparison will
 * fail for reasons that are not a change in how things look.
 *
 * <p>
 * This is the desktop against itself. The browser draws with a different
 * renderer and a different font engine, so the two cannot be compared pixel for
 * pixel - that would need a browser in the loop and a comparison that measures
 * where things are rather than which pixels they are.
 */
public class VisualProbe extends Stage {

  /** Frames to let a scene settle before it is saved. */
  private static final int SETTLE = 20;

  private final FrameRecorder recorder;
  private final String[] scenes = {
      "sprite", "scaled", "turned", "ghost", "say", "think", "text-box", "display",
  };
  private int scene = -1;
  private int frames = 0;

  public VisualProbe(String outputFolder) {
    super(480, 360);
    this.recorder = new FrameRecorder(outputFolder);
    this.setColor(255, 255, 255);
  }

  @Override
  public void run() {
    this.frames++;
    if (this.scene < 0) {
      this.next();
      return;
    }
    if (this.frames < SETTLE) {
      return;
    }
    this.recorder.snapshot(this.scenes[this.scene] + ".png");
    this.next();
  }

  /** Clears the stage and builds the next scene, or leaves when there is none. */
  private void next() {
    this.removeAll();
    this.display("");
    this.scene++;
    this.frames = 0;
    if (this.scene >= this.scenes.length) {
      Window.getInstance().exit();
      return;
    }
    this.build(this.scenes[this.scene]);
  }

  private void build(String name) {
    switch (name) {
      case "sprite" -> {
        // where a sprite sits and how big it is
        this.add(slime(-100, 40));
        this.add(slime(80, -60));
      }
      case "scaled" -> {
        var s = slime(0, 0);
        s.setSize(50);
        this.add(s);
      }
      case "turned" -> {
        // rotation, which getWidth() once measured and should not have
        var s = slime(0, 0);
        s.setDirection(45);
        this.add(s);
      }
      case "ghost" -> {
        // the ghost effect: solid, half, nearly gone
        var solid = slime(-140, 0);
        var half = slime(0, 0);
        half.setTransparency(50);
        var faint = slime(140, 0);
        faint.setTransparency(90);
        this.add(solid);
        this.add(half);
        this.add(faint);
      }
      case "say" -> {
        // the bubble hangs off the top right of the hitbox, not the canvas
        var s = slime(-60, -40);
        this.add(s);
        s.say("Hello");
      }
      case "think" -> {
        var s = slime(-60, -40);
        this.add(s);
        s.think("Hmm");
      }
      case "text-box" -> {
        var t = new Text("A box of words that wraps onto a second line", -200, 60, 260);
        t.setStyle(TextStyle.BOX);
        this.add(t);
      }
      case "display" -> {
        // the band along the bottom, left to right
        this.add(slime(0, 20));
        this.display("A line along the bottom");
      }
      default -> throw new IllegalStateException("no such scene: " + name);
    }
  }

  private static Sprite slime(double x, double y) {
    var s = new Sprite("slime", "slimeGreen");
    s.setPosition(x, y);
    return s;
  }

  public static void main(String[] args) {
    new VisualProbe(args.length > 0 ? args[0] : "frames");
  }
}
