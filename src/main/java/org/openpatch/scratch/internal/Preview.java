package org.openpatch.scratch.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import processing.core.PGraphics;

/**
 * The preview an editor shows of a stage: the program runs as usual until a
 * stage is on screen, that stage takes one step, and the frame it draws is
 * saved with a description of its sprites. Then the program ends.
 *
 * <p>
 * Switched on with system properties, so the program itself stays as it is:
 *
 * <ul>
 * <li>{@code scratch4j.preview=<folder>} writes {@code frame.png} and
 * {@code stage.json} into the folder.</li>
 * <li>{@code scratch4j.preview.stage=<Class>} previews that stage class instead
 * of the first one the program shows. It is made with its constructor without
 * arguments once the program's own first stage is on screen, so the window and
 * whatever {@code main} set up exist.</li>
 * <li>{@code scratch4j.preview.omit=a,b} leaves the sprites in those fields of
 * the stage out of the picture. An editor that draws these itself, to let them
 * be moved, would otherwise show them twice.</li>
 * </ul>
 *
 * <p>
 * The window is not shown, and full screen is ignored. When no stage shows up
 * within {@link #TIMEOUT_MILLIS}, {@code stage.json} says so and the program
 * ends.
 *
 * <p>
 * A capture takes the same picture while the program runs as usual:
 * {@code scratch4j.capture=<folder>} writes {@code <folder>/<Stage>/frame.png}
 * and {@code stage.json} for each stage the first time it has run a step on
 * screen, and the program goes on. {@code scratch4j.capture.omit=Stage:a,b;Other:c}
 * leaves sprites out of the picture per stage class, the way
 * {@code preview.omit} does; the window still shows them.
 */
public final class Preview {

  /** How long a program has to show a stage. */
  static final long TIMEOUT_MILLIS = 30_000;

  private static final String FOLDER = System.getProperty("scratch4j.preview");
  private static final String STAGE = System.getProperty("scratch4j.preview.stage");
  private static final Set<String> OMIT = names(System.getProperty("scratch4j.preview.omit"));
  private static final String CAPTURE = System.getProperty("scratch4j.capture");
  private static final Map<String, Set<String>> CAPTURE_OMIT =
      omitByStage(System.getProperty("scratch4j.capture.omit"));
  /** The stages captured already (once per stage object). */
  private static final Set<Stage> captured =
      java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());

  private static volatile boolean done;
  private static boolean requested;

  private Preview() {}

  /** Whether this run captures the stages it shows (and keeps running). */
  public static boolean capturing() {
    return CAPTURE != null && !CAPTURE.isBlank();
  }

  /** {@code Stage:a,b;Other:c} as the fields to leave out per stage class. */
  private static Map<String, Set<String>> omitByStage(String spec) {
    Map<String, Set<String>> out = new java.util.HashMap<>();
    if (spec != null) {
      for (String part : spec.split(";")) {
        int colon = part.indexOf(':');
        if (colon > 0) out.put(part.substring(0, colon).strip(), names(part.substring(colon + 1)));
      }
    }
    return out;
  }

  /**
   * Called by the render loop after it drew a frame of {@code stage} that ran
   * {@code steps} steps: saves the stage the first time it has run. The
   * picture is drawn once more without the left-out sprites, then the frame is
   * drawn again as it was, so the window shows nothing of it.
   */
  static void capture(Applet applet, Stage stage, StageHooks hooks, PGraphics graphics,
      int steps) {
    if (steps <= 0 || !captured.add(stage)) return;
    String name = stage.getClass().getSimpleName();
    Set<String> omit = CAPTURE_OMIT.getOrDefault(name, java.util.Collections.emptySet());
    try {
      snapshot(applet, stage, hooks, graphics, Path.of(CAPTURE).resolve(name), omit);
    } catch (Throwable t) {
      System.err.println("scratch4j capture of " + name + " failed: " + t);
    }
    hooks.draw(graphics);
  }

  /** Whether this run makes a preview. */
  public static boolean isOn() {
    return FOLDER != null && !FOLDER.isBlank();
  }

  private static Set<String> names(String list) {
    Set<String> out = new LinkedHashSet<>();
    if (list != null) {
      for (String name : list.split(",")) {
        if (!name.isBlank()) out.add(name.strip());
      }
    }
    return out;
  }

  /** Ends the program with an explanation when no stage shows up in time. */
  static void startTimeout() {
    Thread timeout = new Thread(() -> {
      try {
        Thread.sleep(TIMEOUT_MILLIS);
      } catch (InterruptedException e) {
        return;
      }
      if (!done) {
        fail("No stage was shown within " + TIMEOUT_MILLIS / 1000 + " seconds.");
      }
    }, "scratch4j-preview-timeout");
    timeout.setDaemon(true);
    timeout.start();
  }

  /**
   * Called by the render loop with the stage on screen instead of the usual
   * frame. Returns the stage to make current first (the requested one), or
   * null when this frame was the preview.
   */
  static Stage frame(Applet applet, Stage stage, StageHooks hooks, PGraphics graphics) {
    if (done) return null;
    if (STAGE != null && !STAGE.isBlank() && !stage.getClass().getSimpleName().equals(STAGE)
        && !stage.getClass().getName().equals(STAGE)) {
      if (requested) return null; // made, waiting for it to be current
      requested = true;
      return make(stage.getClass().getClassLoader(), stage.getClass().getPackageName());
    }
    done = true;
    try {
      hooks.pre();
      snapshot(applet, stage, hooks, graphics, Path.of(FOLDER), OMIT);
    } catch (Throwable t) {
      fail("The preview could not be drawn: " + t);
    }
    Runtime.getRuntime().halt(0);
    return null;
  }

  /**
   * Draws the stage without the sprites of the {@code omit} fields, and saves
   * the picture and the description of its sprites into {@code folder}. The
   * sprites' visibility is as it was afterwards.
   */
  private static void snapshot(Applet applet, Stage stage, StageHooks hooks, PGraphics graphics,
      Path folder, Set<String> omit) throws IOException {
    Map<Sprite, String> fields = fieldsOf(stage);
    List<Sprite> sprites = hooks.sprites();
    List<Boolean> visible = new ArrayList<>();
    for (Sprite sprite : sprites) {
      visible.add(sprite.isVisible());
      String field = fields.get(sprite);
      if (field != null && omit.contains(field)) sprite.hide();
    }
    try {
      hooks.draw(graphics);
      Files.createDirectories(folder);
      // the picture first: an editor waiting for stage.json then finds both
      stageArea(applet).save(folder.resolve("frame.png").toAbsolutePath().toString());
      write(folder, describe(stage, sprites, visible, fields, applet, omit));
    } finally {
      for (int i = 0; i < sprites.size(); i++) {
        if (visible.get(i)) sprites.get(i).show();
      }
    }
  }

  /**
   * The stage as it is in the window, without the bars around it and at the
   * stage's own size: the window may be scaled (a high-resolution screen, a
   * desktop that sizes windows itself), the stage is letterboxed into it.
   */
  private static processing.core.PImage stageArea(Applet applet) {
    int targetWidth = applet.getRenderWidth();
    int targetHeight = applet.getRenderHeight();
    float ratio = Math.min((float) applet.getWidth() / targetWidth,
        (float) applet.getHeight() / targetHeight);
    int width = Math.round(targetWidth * ratio);
    int height = Math.round(targetHeight * ratio);
    var area = applet.get((applet.getWidth() - width) / 2, (applet.getHeight() - height) / 2,
        width, height);
    if (width != targetWidth || height != targetHeight) {
      area.resize(targetWidth, targetHeight);
    }
    return area;
  }

  /** The requested stage, made the way {@code new MyStage()} would make it. */
  private static Stage make(ClassLoader loader, String packageName) {
    List<String> names = new ArrayList<>();
    names.add(STAGE);
    if (!packageName.isEmpty() && !STAGE.contains(".")) names.add(packageName + "." + STAGE);
    for (String name : names) {
      try {
        Class<?> type = Class.forName(name, true, loader);
        var constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (Stage) constructor.newInstance();
      } catch (ClassNotFoundException e) {
        // try the next name
      } catch (NoSuchMethodException e) {
        fail(STAGE + " has no constructor without arguments, so it cannot be previewed on its own.");
      } catch (ReflectiveOperationException | RuntimeException e) {
        Throwable cause = e.getCause() == null ? e : e.getCause();
        fail("Making " + STAGE + " failed: " + cause);
      }
    }
    fail("There is no stage class " + STAGE + ".");
    return null;
  }

  /** The stage's fields that hold sprites, by sprite: how an editor names them. */
  private static Map<Sprite, String> fieldsOf(Stage stage) {
    Map<Sprite, String> out = new IdentityHashMap<>();
    for (Class<?> type = stage.getClass(); type != null && type != Stage.class;
        type = type.getSuperclass()) {
      for (Field field : type.getDeclaredFields()) {
        if (!Sprite.class.isAssignableFrom(field.getType())) continue;
        try {
          field.setAccessible(true);
          Object value = field.get(stage);
          if (value instanceof Sprite sprite) out.putIfAbsent(sprite, field.getName());
        } catch (ReflectiveOperationException | RuntimeException e) {
          // a field we may not read: the sprite stays unnamed
        }
      }
    }
    return out;
  }

  private static String describe(Stage stage, List<Sprite> sprites, List<Boolean> visible,
      Map<Sprite, String> fields, Applet applet, Set<String> omit) {
    var camera = stage.getCamera();
    StringBuilder json = new StringBuilder("{\n");
    json.append("  \"stage\": ").append(string(stage.getClass().getName())).append(",\n");
    json.append("  \"width\": ").append(applet.getRenderWidth()).append(",\n");
    json.append("  \"height\": ").append(applet.getRenderHeight()).append(",\n");
    json.append("  \"camera\": {\"x\": ").append(number(camera.getX()))
        .append(", \"y\": ").append(number(camera.getY()))
        .append(", \"zoom\": ").append(number(camera.getZoom())).append("},\n");
    json.append("  \"sprites\": [");
    for (int i = 0; i < sprites.size(); i++) {
      Sprite sprite = sprites.get(i);
      String field = fields.get(sprite);
      json.append(i == 0 ? "\n" : ",\n").append("    {")
          .append("\"class\": ").append(string(sprite.getClass().getName()))
          .append(", \"field\": ").append(field == null ? "null" : string(field))
          .append(", \"x\": ").append(number(sprite.getX()))
          .append(", \"y\": ").append(number(sprite.getY()))
          .append(", \"width\": ").append(sprite.getWidth())
          .append(", \"height\": ").append(sprite.getHeight())
          .append(", \"direction\": ").append(number(sprite.getDirection()))
          .append(", \"size\": ").append(number(sprite.getSize()))
          .append(", \"costume\": ").append(string(sprite.getCurrentCostumeName()))
          .append(", \"visible\": ").append(visible.get(i))
          .append(", \"ui\": ").append(isUI(sprite))
          .append(", \"omitted\": ").append(field != null && omit.contains(field))
          .append("}");
    }
    json.append(sprites.isEmpty() ? "]\n" : "\n  ]\n").append("}\n");
    return json.toString();
  }

  private static boolean isUI(Sprite sprite) {
    try {
      Method isUI = Sprite.class.getDeclaredMethod("isUI");
      isUI.setAccessible(true);
      return (Boolean) isUI.invoke(sprite);
    } catch (ReflectiveOperationException | RuntimeException e) {
      return false;
    }
  }

  private static void write(Path folder, String json) throws IOException {
    Files.writeString(folder.resolve("stage.json"), json, StandardCharsets.UTF_8);
  }

  /** Writes why there is no preview, and ends the program. */
  static void fail(String reason) {
    done = true;
    try {
      Path folder = Path.of(FOLDER);
      Files.createDirectories(folder);
      Files.deleteIfExists(folder.resolve("frame.png"));
      write(folder, "{\n  \"error\": " + string(reason) + "\n}\n");
    } catch (IOException e) {
      System.err.println("scratch4j preview: " + reason);
    }
    Runtime.getRuntime().halt(2);
  }

  private static String number(double value) {
    return value == Math.rint(value) && Math.abs(value) < 1e15
        ? Long.toString((long) value) : Double.toString(value);
  }

  private static String string(String value) {
    if (value == null) return "null";
    StringBuilder out = new StringBuilder("\"");
    for (char c : value.toCharArray()) {
      switch (c) {
        case '"' -> out.append("\\\"");
        case '\\' -> out.append("\\\\");
        case '\n' -> out.append("\\n");
        case '\r' -> out.append("\\r");
        case '\t' -> out.append("\\t");
        default -> {
          if (c < 0x20) out.append(String.format("\\u%04x", (int) c));
          else out.append(c);
        }
      }
    }
    return out.append('"').toString();
  }
}
