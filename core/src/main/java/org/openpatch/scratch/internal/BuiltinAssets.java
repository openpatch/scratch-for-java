package org.openpatch.scratch.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The sprites bundled with the library (Kenney's CC0 sheets), by name: {@code "slimeGreen"} or,
 * when a name is in two sheets, {@code "platformer/slimeGreen"}. Read from the sheets' texture
 * atlases ({@code images/<sheet>.xml}).
 */
public final class BuiltinAssets {

  private static final String[] SHEETS = {"platformer", "jumper", "space_shooter", "tappy_plane", "cat"};
  private static final String BASE = "images/";
  /** One {@code <SubTexture … />}: the atlases are machine-written, so attributes are regular. */
  private static final Pattern SUB_TEXTURE = Pattern.compile("<SubTexture\\s([^>]*?)/?>");
  private static final Pattern ATTRIBUTE = Pattern.compile("(\\w+)\\s*=\\s*\"([^\"]*)\"");

  /** A sprite of a sheet: its region and the way it is drawn facing (0 up, 90 right, …). */
  public record Entry(String name, String sheet, int x, int y, int width, int height, double direction) {
    public String sheetPath() {
      return BASE + this.sheet + ".png";
    }
  }

  private static Map<String, Entry> assets;

  private BuiltinAssets() {}

  private static synchronized Map<String, Entry> assets() {
    if (assets != null) {
      return assets;
    }
    Map<String, Entry> loaded = new LinkedHashMap<>();
    for (String sheet : SHEETS) {
      // through the class, not its loader: on the module path images/ is a package of the module
      try (InputStream in = BuiltinAssets.class.getResourceAsStream("/" + BASE + sheet + ".xml")) {
        if (in == null) {
          continue;
        }
        String xml = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        Matcher sub = SUB_TEXTURE.matcher(xml);
        while (sub.find()) {
          Map<String, String> a = new LinkedHashMap<>();
          Matcher attr = ATTRIBUTE.matcher(sub.group(1));
          while (attr.find()) {
            a.put(attr.group(1), attr.group(2));
          }
          String name = a.get("name");
          if (name == null) {
            continue;
          }
          String simple = Suggestions.stripExtension(name);
          String qualified = key(sheet + "/" + simple);
          if (loaded.containsKey(qualified)) {
            continue; // a few sprites are listed twice in the same atlas: keep the first
          }
          Entry entry = new Entry(simple, sheet, Integer.parseInt(a.get("x")), Integer.parseInt(a.get("y")),
              Integer.parseInt(a.get("width")), Integer.parseInt(a.get("height")),
              a.containsKey("direction") ? Double.parseDouble(a.get("direction")) : 90);
          loaded.put(qualified, entry);
          loaded.putIfAbsent(key(simple), entry); // the qualified name is unique, the bare one first-come
        }
      } catch (IOException | RuntimeException e) {
        // a broken atlas must never stop a program: only its sprites are missing
      }
    }
    assets = loaded;
    return assets;
  }

  private static String key(String name) {
    return Suggestions.normalise(name);
  }

  /** "platformer/grass" is a built-in name, "sprites/cat.png" a path: the extension tells. */
  public static boolean isBuiltinName(String name) {
    if (name == null || name.isEmpty() || name.indexOf('\\') >= 0 || name.startsWith("~") || name.startsWith("/")) {
      return false;
    }
    String last = name.substring(name.lastIndexOf('/') + 1);
    return last.indexOf('.') < 0;
  }

  public static Entry get(String name) {
    return name == null ? null : assets().get(key(name));
  }

  public static List<String> getNames() {
    List<String> names = new ArrayList<>();
    for (Entry e : new LinkedHashSet<>(assets().values())) {
      if (!names.contains(e.name())) {
        names.add(e.name());
      }
    }
    return names;
  }

  public static List<Entry> getEntries() {
    return new ArrayList<>(new LinkedHashSet<>(assets().values()));
  }

  /** The bundled sheets, in lookup order (the docs' asset pages list them). */
  public static List<String> getSheets() {
    return List.of(SHEETS);
  }

  /**
   * The name to write in code for a sprite: its bare name, unless an earlier sheet already claims
   * it, then {@code sheet/name}.
   */
  public static String getReferenceName(Entry entry) {
    Entry bare = get(entry.name());
    return bare == entry ? entry.name() : entry.sheet() + "/" + entry.name();
  }

  /** Names close to a mistyped one, best first, for "did you mean …?". */
  public static List<String> suggest(String name, int limit) {
    return Suggestions.closestTo(getNames(), name, limit);
  }
}
