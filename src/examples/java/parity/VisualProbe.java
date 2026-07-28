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
      // sprites
      "sprite", "scaled", "turned", "ghost", "tint", "rotation-styles", "costumes",
      // what a sprite says
      "say", "think", "say-wraps",
      // text of its own
      "text-plain", "text-box", "text-speak", "text-think", "text-align", "text-colours",
      // the rest of what a stage draws
      "backdrop", "pen", "stamps", "display", "debug", "camera",
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
    // whatever the last scene turned on, so that the next one starts clean
    this.setDebug(false);
    this.getCamera().setPosition(0, 0);
    this.getCamera().resetZoom();
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
      case "tint" -> {
        // the colour effect, by hue and by rgb
        var plain = slime(-140, 0);
        var hue = slime(0, 0);
        hue.setTint(160);
        var rgb = slime(140, 0);
        rgb.setTint(255, 120, 120);
        this.add(plain);
        this.add(hue);
        this.add(rgb);
      }
      case "rotation-styles" -> {
        // the same direction drawn three ways
        var around = slime(-140, 0);
        around.setRotationStyle(RotationStyle.ALL_AROUND);
        around.setDirection(200);
        var leftRight = slime(0, 0);
        leftRight.setRotationStyle(RotationStyle.LEFT_RIGHT);
        leftRight.setDirection(200);
        var dont = slime(140, 0);
        dont.setRotationStyle(RotationStyle.DONT);
        dont.setDirection(200);
        this.add(around);
        this.add(leftRight);
        this.add(dont);
      }
      case "costumes" -> {
        // a second costume, switched to by name
        var s = slime(0, 0);
        s.addCostume("blue", "slimeBlue");
        s.switchCostume("blue");
        this.add(s);
      }
      case "say-wraps" -> {
        // long enough to wrap, which sizes the bubble rather than the words
        var s = slime(-40, -60);
        this.add(s);
        s.say("A sentence long enough that the bubble has to grow to hold it");
      }
      case "text-plain" -> {
        var t = new Text("Plain words, no box", -200, 40, 260);
        this.add(t);
      }
      case "text-box" -> {
        var t = new Text("A box of words that wraps onto a second line", -200, 60, 260);
        t.setStyle(TextStyle.BOX);
        this.add(t);
      }
      case "text-speak" -> {
        // a speech bubble with no sprite behind it, which once drew nothing
        var t = new Text("Speaking without a sprite", -120, 40, 240);
        t.setStyle(TextStyle.SPEAK);
        this.add(t);
      }
      case "text-think" -> {
        var t = new Text("Thinking without a sprite", -120, 40, 240);
        t.setStyle(TextStyle.THINK);
        this.add(t);
      }
      case "text-align" -> {
        // three texts on the same x, laid out three ways
        this.add(aligned("left", TextAlign.LEFT, 90));
        this.add(aligned("centre", TextAlign.CENTER, 20));
        this.add(aligned("right", TextAlign.RIGHT, -50));
      }
      case "text-colours" -> {
        var t = new Text("Words, box and edge, each its own colour", -200, 50, 260);
        t.setStyle(TextStyle.BOX);
        t.setTextColor(20, 40, 120);
        t.setBackgroundColor(250, 240, 180);
        t.setStrokeColor(200, 60, 60);
        this.add(t);
      }
      case "backdrop" -> {
        // stretched to the stage rather than centred at its own size
        this.addBackdrop("forest", "background", true);
        this.add(slime(0, -20));
      }
      case "pen" -> {
        var pen = new Pen();
        this.add(pen);
        pen.setSize(6);
        pen.setColor(200, 40, 40);
        pen.setPosition(-160, -80);
        pen.down();
        pen.setPosition(-60, 80);
        pen.setPosition(40, -80);
        pen.setPosition(140, 80);
        pen.up();
        pen.setSize(2);
        pen.setColor(30, 80, 200);
        pen.setPosition(-160, 0);
        pen.down();
        pen.setPosition(160, 0);
        pen.up();
      }
      case "stamps" -> {
        // prints of a sprite left behind on the background
        var s = slime(-140, 0);
        this.add(s);
        s.stamp();
        s.setPosition(0, 40);
        s.stamp();
        s.setPosition(140, -40);
      }
      case "debug" -> {
        // the hitbox outline, the direction and the position
        this.setDebug(true);
        var s = slime(-60, 20);
        s.setDirection(135);
        this.add(s);
      }
      case "camera" -> {
        // the whole stage seen closer and off centre
        this.add(slime(-80, 30));
        this.add(slime(90, -40));
        this.getCamera().setPosition(40, 0);
        this.getCamera().setZoom(1.6);
      }
      case "display" -> {
        // the band along the bottom, left to right
        this.add(slime(0, 20));
        this.display("A line along the bottom");
      }
      default -> throw new IllegalStateException("no such scene: " + name);
    }
  }

  /** A text at a fixed x, laid out the given way, for the alignment scene. */
  private static Text aligned(String words, TextAlign align, double y) {
    var t = new Text(words, 0, y, 200);
    t.setAlign(align);
    return t;
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
