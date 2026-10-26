package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openpatch.scratch.internal.Game;

/**
 * Every value that has to read the same on the desktop and in the browser (6.x
 * {@code parity/ParityProbe}, now headless).
 *
 * <p>The two implementations have drifted apart more than once, always in a way no signature could
 * catch: a transparency counting the other way round, a getWidth() measuring the rotated bounding
 * box, a hitbox in screen pixels. The values are recorded in
 * {@code src/test/resources/parity/expected.txt}; the online IDE runs the same probe against the
 * same file. Nothing here may depend on timing or randomness.
 *
 * <p>Re-record after a deliberate change: {@code mvn test -pl core -Dtest=ParityTest
 * -Dparity.record=true}, then read the diff of the file.
 */
class ParityTest {

  static final Path EXPECTED = Path.of("src/test/resources/parity/expected.txt");

  Game game;
  final List<String> lines = new ArrayList<>();

  @BeforeEach
  void setUp() {
    this.game = Game.headless(600, 240);
  }

  @AfterEach
  void tearDown() {
    this.game.setStage(null);
  }

  void p(String key, Object value) {
    this.lines.add(key + "=" + value);
  }

  @Test
  void valuesAreTheRecordedOnes() throws IOException {
    this.probe();
    if (Boolean.getBoolean("parity.record")) {
      Files.createDirectories(EXPECTED.getParent());
      Files.write(EXPECTED, this.lines, StandardCharsets.UTF_8);
    }
    assertEquals(String.join("\n", Files.readAllLines(EXPECTED, StandardCharsets.UTF_8)),
        String.join("\n", this.lines),
        "a parity value changed; if on purpose, re-record with -Dparity.record=true and read the diff");
  }

  void probe() {
    Stage myStage = new Stage();
    this.game.setStage(myStage);
    Sprite s = new Sprite("zeta", "slimeGreen");
    myStage.add(s);

    p("stage.width", myStage.getWidth());
    p("stage.height", myStage.getHeight());

    // a new sprite: the defaults everything else is measured against
    p("sprite.x", s.getX());
    p("sprite.y", s.getY());
    p("sprite.direction", s.getDirection());
    p("sprite.size", s.getSize());
    p("sprite.transparency", s.getTransparency());
    p("sprite.width", s.getWidth());
    p("sprite.height", s.getHeight());

    // the costume's own pixels, scaled - never the rotated bounding box
    s.setSize(50);
    p("half.width", s.getWidth());
    p("half.height", s.getHeight());
    p("half.size", s.getSize());
    s.setSize(100);
    s.setDirection(45);
    p("turned.width", s.getWidth());
    p("turned.height", s.getHeight());
    s.setDirection(90);

    // transparency is Scratch's ghost effect: 0 solid, 100 invisible
    s.setTransparency(30);
    p("ghost30", s.getTransparency());
    s.changeTransparency(50);
    p("ghost30plus50", s.getTransparency());
    s.changeTransparency(50);
    p("ghost.clamped.high", s.getTransparency());
    s.setTransparency(-20);
    p("ghost.clamped.low", s.getTransparency());
    s.setTransparency(0);

    // movement and turning
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

    // the hitbox hugs the painted pixels rather than the whole costume canvas
    Bounds b = s.getHitbox().getBounds();
    p("hitbox", b);
    p("hitbox.x", b.x());
    p("hitbox.y", b.y());
    p("hitbox.width", b.width());
    p("hitbox.height", b.height());

    // costumes
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
    p("text.style", t.getStyle());
    p("text.defaultSize", Text.getDefaultFontSize());

    p("count", myStage.count(Sprite.class));
    p("find", myStage.find(Sprite.class).size());

    this.valuesWithoutAStage();
  }

  /** What the browser's node tests can check too: no stage needed. */
  void valuesWithoutAStage() {
    // operators are the Scratch blocks, not Java's arithmetic
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
    // 6.x new Color(128) was hue 128 of 255; 7.0 counts hue 0-100 as Scratch does
    Color hue = Color.fromHSB(128 / 2.55, 100, 100);
    p("hue.red", hue.getRed());
    p("hue.green", hue.getGreen());
    p("hue.blue", hue.getBlue());
  }
}
