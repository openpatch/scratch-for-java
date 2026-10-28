package org.openpatch.scratch.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.internal.platform.Platform;

/**
 * What Studio's stage designer needs from a running program: {@code frame.png} (the stage at stage
 * size) and {@code stage.json} (its sprites, which fields hold them, where they are).
 *
 * <ul>
 *   <li>{@code -Dscratch4j.preview=<folder>}: the first frame of the stage (or of the stage class
 *       {@code -Dscratch4j.preview.stage=<Name>}), then the program ends; the sprites in the
 *       fields {@code -Dscratch4j.preview.omit=a,b} are left out of the picture.
 *   <li>{@code -Dscratch4j.capture=<folder>}: the first frame of every stage into
 *       {@code <folder>/<StageClass>/} while the program keeps running
 *       ({@code -Dscratch4j.capture.omit=Stage:a,b;Other:c}).
 * </ul>
 *
 * <p>The description is made on the game thread (it reads the stage's fields); the picture is
 * taken on the window thread from the draw list made right after it.
 */
public final class Preview {

  static final long TIMEOUT_MILLIS = 30_000;
  private static final String FOLDER = System.getProperty("scratch4j.preview");
  private static final String STAGE = System.getProperty("scratch4j.preview.stage");
  private static final Set<String> OMIT = names(System.getProperty("scratch4j.preview.omit"));
  private static final String CAPTURE = System.getProperty("scratch4j.capture");
  private static final Map<String, Set<String>> CAPTURE_OMIT = omitByStage(System.getProperty("scratch4j.capture.omit"));

  /** A picture to take: where, and the description to write next to it. */
  public record Shot(int stageId, Path folder, String json, boolean thenExit) {}

  private static final Set<Integer> described = Collections.synchronizedSet(new LinkedHashSet<>());
  private static volatile Shot pending;
  private static volatile boolean done;
  private static boolean requested;
  private static List<Sprite> hidden = List.of();

  private Preview() {}

  public static boolean isPreview() {
    return FOLDER != null && !FOLDER.isBlank();
  }

  public static boolean isCapture() {
    return CAPTURE != null && !CAPTURE.isBlank();
  }

  /** Window thread, once: a preview that never gets a stage still ends, with a message for Studio. */
  public static void startTimeout() {
    if (!isPreview()) {
      return;
    }
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
   * Game thread, after a batch and before its draw list: describes the stage the first time it is
   * shown (after its first tick), hides omitted sprites for the picture.
   */
  public static void beforeDraw(Game game) {
    if (done || (!isPreview() && !isCapture()) || game.getTickCount() == 0) {
      return;
    }
    Stage stage = game.getStage();
    if (stage == null) {
      return;
    }
    String name = stage.getClass().getSimpleName();
    if (isPreview() && STAGE != null && !STAGE.isBlank() && !name.equals(STAGE)
        && !stage.getClass().getName().equals(STAGE)) {
      if (!requested) {
        requested = true;
        game.setStage(make(stage.getClass().getClassLoader(), packageOf(stage.getClass())));
      }
      return;
    }
    int id = stageId(game);
    if (!described.add(id)) {
      return;
    }
    Set<String> omit = isPreview() ? OMIT : CAPTURE_OMIT.getOrDefault(name, Set.of());
    Map<Sprite, String> fields = fieldsOf(stage);
    List<Sprite> sprites = Game.spritesOf(stage);
    List<Boolean> visible = new ArrayList<>();
    List<Sprite> hide = new ArrayList<>();
    for (Sprite s : sprites) {
      visible.add(s.isVisible());
      String field = fields.get(s);
      if (field != null && omit.contains(field) && s.isVisible()) {
        hide.add(s);
      }
    }
    String json = describe(game, stage, sprites, visible, fields, omit);
    for (Sprite s : hide) {
      s.hide();
    }
    hidden = hide;
    Path folder = isPreview() ? Path.of(FOLDER) : Path.of(CAPTURE).resolve(name);
    pending = new Shot(id, folder, json, isPreview());
    if (isPreview()) {
      done = true;
    }
  }

  /** Game thread, right after the draw list: omitted sprites show again. */
  public static void afterDraw() {
    for (Sprite s : hidden) {
      s.show();
    }
    hidden = List.of();
  }

  /** Window thread: the picture to take for this draw list, if any (taken once). */
  public static Shot take(int stageId) {
    Shot shot = pending;
    if (shot != null && shot.stageId() == stageId) {
      pending = null;
      return shot;
    }
    return null;
  }

  /** Writes {@code stage.json} after {@code frame.png}: an editor waiting for it then finds both. */
  public static void write(Shot shot) throws IOException {
    Files.createDirectories(shot.folder());
    Files.writeString(shot.folder().resolve("stage.json"), shot.json(), StandardCharsets.UTF_8);
  }

  private static int stageId(Game game) {
    return game.drawListStageId();
  }

  private static Stage make(ClassLoader loader, String packageName) {
    List<String> names = new ArrayList<>();
    names.add(STAGE);
    if (!packageName.isEmpty() && !STAGE.contains(".")) {
      names.add(packageName + "." + STAGE);
    }
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

  /** Which field of the stage holds which sprite: the stage designer edits the constructor by them. */
  private static Map<Sprite, String> fieldsOf(Stage stage) {
    Map<Sprite, String> out = new IdentityHashMap<>();
    for (Class<?> type = stage.getClass(); type != null && type != Stage.class; type = type.getSuperclass()) {
      for (Field field : type.getDeclaredFields()) {
        if (!Sprite.class.isAssignableFrom(field.getType())) {
          continue;
        }
        try {
          field.setAccessible(true);
          if (field.get(stage) instanceof Sprite sprite) {
            out.putIfAbsent(sprite, field.getName());
          }
        } catch (ReflectiveOperationException | RuntimeException e) {
          // a field we may not read: the sprite stays unnamed
        }
      }
    }
    return out;
  }

  private static String describe(Game game, Stage stage, List<Sprite> sprites, List<Boolean> visible,
      Map<Sprite, String> fields, Set<String> omit) {
    var camera = stage.getCamera();
    StringBuilder json = new StringBuilder("{\n");
    json.append("  \"stage\": ").append(string(stage.getClass().getName())).append(",\n");
    json.append("  \"width\": ").append(game.getWidth()).append(",\n");
    json.append("  \"height\": ").append(game.getHeight()).append(",\n");
    json.append("  \"camera\": {\"x\": ").append(number(camera.getX())).append(", \"y\": ")
        .append(number(camera.getY())).append(", \"zoom\": ").append(number(camera.getZoom())).append("},\n");
    json.append("  \"sprites\": [");
    for (int i = 0; i < sprites.size(); i++) {
      Sprite s = sprites.get(i);
      String field = fields.get(s);
      json.append(i == 0 ? "\n" : ",\n").append("    {")
          .append("\"class\": ").append(string(s.getClass().getName()))
          .append(", \"field\": ").append(field == null ? "null" : string(field))
          .append(", \"x\": ").append(number(s.getX()))
          .append(", \"y\": ").append(number(s.getY()))
          .append(", \"width\": ").append(number(s.getWidth()))
          .append(", \"height\": ").append(number(s.getHeight()))
          .append(", \"direction\": ").append(number(s.getDirection()))
          .append(", \"size\": ").append(number(s.getSize()))
          .append(", \"costume\": ").append(string(s.getCurrentCostumeName()))
          .append(", \"visible\": ").append(visible.get(i))
          .append(", \"ui\": ").append(s.getLayer() == Layer.UI)
          .append(", \"layer\": ").append(string(s.getLayer().name()))
          .append(", \"omitted\": ").append(field != null && omit.contains(field))
          .append("}");
    }
    json.append(sprites.isEmpty() ? "]\n" : "\n  ]\n").append("}\n");
    return json.toString();
  }

  /** Ends a preview that cannot be made: {@code stage.json} holds the reason, for Studio to show. */
  static void fail(String reason) {
    done = true;
    try {
      Path folder = Path.of(FOLDER);
      Files.createDirectories(folder);
      Files.deleteIfExists(folder.resolve("frame.png"));
      Files.writeString(folder.resolve("stage.json"), "{\n  \"error\": " + string(reason) + "\n}\n",
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      System.err.println("scratch4j preview: " + reason);
    }
    Platform.get().halt(2);
  }

  private static Set<String> names(String list) {
    Set<String> out = new LinkedHashSet<>();
    if (list != null) {
      for (String name : list.split(",")) {
        if (!name.isBlank()) {
          out.add(name.strip());
        }
      }
    }
    return out;
  }

  private static Map<String, Set<String>> omitByStage(String spec) {
    Map<String, Set<String>> out = new HashMap<>();
    if (spec != null) {
      for (String part : spec.split(";")) {
        int colon = part.indexOf(':');
        if (colon > 0) {
          out.put(part.substring(0, colon).strip(), names(part.substring(colon + 1)));
        }
      }
    }
    return out;
  }

  private static String number(double value) {
    return value == Math.rint(value) && Math.abs(value) < 1e15 ? Long.toString((long) value)
        : Double.toString(value);
  }

  private static String string(String value) {
    if (value == null) {
      return "null";
    }
    StringBuilder out = new StringBuilder("\"");
    for (char c : value.toCharArray()) {
      switch (c) {
        case '"' -> out.append("\\\"");
        case '\\' -> out.append("\\\\");
        case '\n' -> out.append("\\n");
        case '\r' -> out.append("\\r");
        case '\t' -> out.append("\\t");
        default -> {
          if (c < 0x20) {
            out.append(String.format("\\u%04x", (int) c));
          } else {
            out.append(c);
          }
        }
      }
    }
    return out.append('"').toString();
  }

  private static String packageOf(Class<?> type) {
    String name = type.getName();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? "" : name.substring(0, dot);
  }
}
