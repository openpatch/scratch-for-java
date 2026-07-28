package parity;

import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;
import com.github.romankh3.image.comparison.model.ImageComparisonResult;
import com.github.romankh3.image.comparison.model.ImageComparisonState;
import com.github.romankh3.image.comparison.model.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Compares the scenes {@link VisualProbe} has just drawn with the ones recorded
 * earlier, and says what moved.
 *
 * <p>
 * Run by {@code scripts/visual-parity.sh}, which draws the scenes first - that
 * part needs a window, and so Xvfb. This half needs nothing but the two folders
 * of PNGs.
 *
 * <p>
 * The comparison is exact. Two runs of the desktop renderer are bit-identical,
 * so there is no rasterising noise to allow for, and any difference at all is a
 * real change in what is drawn. Where a scene does differ, a copy of it is
 * written out with a box drawn round each region that moved, which is a quicker
 * thing to read than a count of pixels.
 */
public final class VisualParity {

  /** A difference of a single pixel is a difference. */
  private static final double PIXEL_TOLERANCE = 0.0;

  /** Boxes smaller than this are noise rather than something that moved. */
  private static final int SMALLEST_BOX = 4;

  /**
   * Corners of a scene that are allowed to differ, because what is drawn there
   * is not the same twice however still the scene is.
   *
   * <p>
   * There is one: the debug overlay prints the frame rate, which is a different
   * number on every run. The rest of that scene - the hitbox outline, the
   * direction, the position - is worth keeping an eye on, so the frame counter
   * is cut out rather than the whole scene being given up.
   */
  private static final Map<String, List<Rectangle>> ALLOWED_TO_DIFFER = Map.of(
      "debug", List.of(new Rectangle(10, 4, 210, 30)));

  private VisualParity() {
  }

  public static void main(String[] args) {
    if (args.length < 3) {
      System.err.println("usage: VisualParity <recorded-dir> <drawn-dir> <where-to-put-differences>");
      System.exit(2);
    }
    File recordedDir = new File(args[0]);
    File drawnDir = new File(args[1]);
    File differencesDir = new File(args[2]);

    var recorded = scenesIn(recordedDir);
    var drawn = scenesIn(drawnDir);
    if (drawn.isEmpty()) {
      System.err.println("visual-parity: nothing was drawn - the probe probably failed to start.");
      System.exit(1);
    }

    var everyScene = new TreeSet<String>();
    everyScene.addAll(recorded);
    everyScene.addAll(drawn);

    List<String> changed = new ArrayList<>();
    for (String scene : everyScene) {
      if (!recorded.contains(scene)) {
        System.out.println("  " + scene + ": drawn, but never recorded");
        changed.add(scene);
        continue;
      }
      if (!drawn.contains(scene)) {
        System.out.println("  " + scene + ": recorded, but nothing drew it this time");
        changed.add(scene);
        continue;
      }
      if (!compare(scene, new File(recordedDir, scene + ".png"),
          new File(drawnDir, scene + ".png"), differencesDir)) {
        changed.add(scene);
      }
    }

    if (changed.isEmpty()) {
      System.out.println("visual-parity: all " + drawn.size()
          + " scenes look the way they were recorded.");
      return;
    }

    System.out.println();
    System.out.println("visual-parity: what is drawn has changed.");
    System.out.println("        - " + differencesDir.getPath()
        + " has each one, boxed where it moved");
    System.out.println("        - if the change is meant, ./scripts/visual-parity.sh --record");
    System.exit(1);
  }

  /** Whether the scene still looks the same, writing out the difference if not. */
  private static boolean compare(String scene, File recorded, File drawn, File differencesDir) {
    var expected = ImageComparisonUtil.readImageFromResources(recorded.getAbsolutePath());
    var actual = ImageComparisonUtil.readImageFromResources(drawn.getAbsolutePath());

    differencesDir.mkdirs();
    ImageComparisonResult result = new ImageComparison(expected, actual,
        new File(differencesDir, scene + ".png"))
        .setPixelToleranceLevel(PIXEL_TOLERANCE)
        .setMinimalRectangleSize(SMALLEST_BOX)
        .setExcludedAreas(ALLOWED_TO_DIFFER.getOrDefault(scene, List.of()))
        .compareImages();

    ImageComparisonState state = result.getImageComparisonState();
    if (state == ImageComparisonState.MATCH) {
      return true;
    }
    if (state == ImageComparisonState.SIZE_MISMATCH) {
      System.out.println("  " + scene + ": the frame is a different size than it was ("
          + expected.getWidth() + "x" + expected.getHeight() + " recorded, "
          + actual.getWidth() + "x" + actual.getHeight() + " now)");
      return false;
    }
    System.out.printf("  %s: %.2f%% of the frame is different%n",
        scene, result.getDifferencePercent());
    return false;
  }

  /** The scene names in a folder, taken from the PNGs in it. */
  private static TreeSet<String> scenesIn(File dir) {
    File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
    var names = new TreeSet<String>();
    if (files != null) {
      Arrays.stream(files)
          .map(f -> f.getName().substring(0, f.getName().length() - ".png".length()))
          .forEach(names::add);
    }
    return names;
  }
}
