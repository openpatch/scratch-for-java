package parity;

import org.openpatch.scratch.*;

/**
 * Every value that has to read the same on the desktop and in the browser.
 *
 * <p>
 * The two implementations of this library have drifted apart more than once, and
 * always in a way no signature could catch: a transparency that counted the
 * other way round, a getWidth() that measured the rotated bounding box instead
 * of the costume, a hitbox reported in screen pixels rather than around the
 * middle of the stage. Every one of those was found by hand, by running the same
 * program twice and reading the two outputs side by side. This is that program,
 * written down.
 *
 * <p>
 * Nothing here may depend on timing, randomness or the frame rate - the browser
 * runs it in a different loop and would disagree for reasons that are not drift.
 *
 * <p>
 * The expected output lives in {@code src/test/resources/parity/expected.txt}.
 * {@code ./scripts/parity.sh} checks the desktop against it, and
 * {@code scripts/parity-fixture.py} generates the copy the online IDE runs as
 * {@code ScratchParityTest.java} in its own test suite.
 */
public class ParityProbe {

  static void p(String key, Object value) {
    System.out.println(key + "=" + value);
  }

  public static void main(String[] args) {
    Stage myStage = new Stage(600, 240);
    Sprite s = new Sprite("zeta", "slimeGreen");
    myStage.add(s);

    p("stage.width", myStage.getWidth());
    p("stage.height", myStage.getHeight());

    // A new sprite: the defaults everything else is measured against.
    p("sprite.x", s.getX());
    p("sprite.y", s.getY());
    p("sprite.direction", s.getDirection());
    p("sprite.size", s.getSize());
    p("sprite.transparency", s.getTransparency());
    p("sprite.width", s.getWidth());
    p("sprite.height", s.getHeight());

    // The costume's own pixels, scaled - never the rotated bounding box.
    s.setSize(50);
    p("half.width", s.getWidth());
    p("half.height", s.getHeight());
    p("half.size", s.getSize());
    s.setSize(100);
    s.setDirection(45);
    p("turned.width", s.getWidth());
    p("turned.height", s.getHeight());
    s.setDirection(90);

    // Transparency is the Scratch ghost effect: 0 solid, 100 invisible.
    s.setTransparency(30);
    p("ghost30", s.getTransparency());
    s.changeTransparency(50);
    p("ghost30plus50", s.getTransparency());
    s.changeTransparency(50);
    p("ghost.clamped.high", s.getTransparency());
    s.setTransparency(-20);
    p("ghost.clamped.low", s.getTransparency());
    s.setTransparency(0);

    // Movement and turning.
    s.setPosition(10, 20);
    p("moved.x", s.getX());
    p("moved.y", s.getY());
    s.changeX(5);
    s.changeY(-5);
    p("changed.x", s.getX());
    p("changed.y", s.getY());
    s.changePosition(-15, -15);
    p("back.x", s.getX());
    p("back.y", s.getY());
    s.setDirection(0);
    s.turnLeft(30);
    p("turnLeft30", s.getDirection());
    s.turnRight(60);
    p("turnRight60", s.getDirection());
    s.setDirection(-90);
    p("directionMinus90", s.getDirection());
    s.setDirection(90);
    s.move(10);
    p("move10.x", s.getX());
    p("move10.y", s.getY());
    s.setPosition(0, 0);

    // The hitbox is around the middle of the stage, y pointing down, and hugs
    // the painted pixels rather than the whole costume canvas.
    Bounds b = s.getHitbox().getBounds();
    p("hitbox", b);
    p("hitbox.x", b.x());
    p("hitbox.y", b.y());
    p("hitbox.width", b.width());
    p("hitbox.height", b.height());

    // Costumes.
    p("costume.index", s.getCurrentCostumeIndex());
    p("costume.name", s.getCurrentCostumeName());
    s.addCostume("second", "slimeBlue");
    s.nextCostume();
    p("next.index", s.getCurrentCostumeIndex());
    p("next.name", s.getCurrentCostumeName());
    s.switchCostume(0);
    p("switched.index", s.getCurrentCostumeIndex());
    s.previousCostume();
    p("previous.index", s.getCurrentCostumeIndex());

    p("pen.size", s.getPen().getSize());
    p("volume", s.getVolume());
    p("stage.volume", myStage.getVolume());

    Text t = new Text("Hello", 0, 0, 200);
    myStage.add(t);
    p("text.size", t.getTextSize());
    p("text.width", t.getWidth());
    p("text.align", t.getAlign());
    // a text built this way once had no style at all, and drawing one threw a
    // NullPointerException on every frame from inside the loading screen
    p("text.style", t.getStyle());
    p("text.defaultSize", Text.getDefaultFontSize());

    p("count", myStage.count(Sprite.class));
    p("find", myStage.find(Sprite.class).size());

    valuesWithoutAStage();

    myStage.wait(100);
    Window.getInstance().exit();
  }

  /**
   * The part of the probe that needs no window.
   *
   * <p>
   * The browser's test suite runs in node, where there is no WebGL, so a Stage
   * cannot be built there at all - only what is below this line can be checked
   * automatically on both sides. Everything above it is held to the recorded
   * values on the desktop by {@code scripts/parity.sh}, and to the same
   * signatures in the browser by {@code ScratchTest.java}; the values themselves
   * still have to be read by eye there.
   *
   * <p>
   * Anything that can live down here should, because down here it is checked by
   * a machine.
   */
  static void valuesWithoutAStage() {
    // Operators are the Scratch blocks, not Java's arithmetic.
    p("round", Operators.round(2.5));
    p("roundTo2", Operators.round(2.345, 2));
    p("mod", Operators.mod(-7, 3));
    p("modPositive", Operators.mod(7, 3));
    p("modWraps", Operators.mod(-1, 10));
    p("map", Operators.map(5, 0, 10, 0, 100));
    p("constrain", Operators.constrain(15, 0, 10));
    p("lerp", Operators.lerp(0, 10, 0.25));
    p("abs", Operators.absOf(-3));
    p("sqrt", Operators.sqrtOf(4));
    p("sin30", Operators.round(Operators.sinOf(30), 4));
    p("cos60", Operators.round(Operators.cosOf(60), 4));
    p("atan1", Operators.atanOf(1));
    p("log100", Operators.logOf(100));
    p("floor", Operators.floorOf(-2.5));
    p("ceiling", Operators.ceilingOf(-2.5));
    p("min", Operators.min(3, 1, 2));
    p("max", Operators.max(3, 1, 2));

    Vector2 v = new Vector2(3, 4);
    p("vector", v);
    p("vector.length", v.length());
    p("vector.angle", v.angle());
    p("vector.unit", v.unitVector());
    p("vector.add", new Vector2(1, 2).add(new Vector2(3, 4)));
    p("vector.dot", new Vector2(1, 2).dot(new Vector2(3, 4)));

    Color c = new Color(255, 128, 0);
    p("color", c);
    p("color.red", c.getRed());
    p("color.green", c.getGreen());
    p("color.blue", c.getBlue());
    p("color.equals", c.equals(new Color(255, 128, 0)));
    Color hue = new Color(128);
    p("hue.red", hue.getRed());
    p("hue.green", hue.getGreen());
    p("hue.blue", hue.getBlue());
  }
}
