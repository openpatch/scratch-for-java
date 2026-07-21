package org.openpatch.scratch.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Registry for the sprite sheets that ship inside the Scratch for Java jar.
 *
 * <p>
 * The bundled artwork comes from <a href="https://kenney.nl">kenney.nl</a> and
 * is released under CC0, so it can be used for any purpose without attribution.
 * Each sheet has an XML atlas next to it describing where every single sprite
 * sits on the sheet; this class reads those atlases and lets a sprite be
 * addressed by plain name:
 *
 * <pre>{@code
 * sprite.addCostume("bunny1_jump");
 * }</pre>
 *
 * <p>
 * A name may be given bare ({@code "cactus"}) or qualified with its sheet
 * ({@code "jumper/cactus"}). Three names - cactus, rock and spring - occur on
 * more than one sheet; for those, the bare form resolves against the first
 * sheet in {@link #SHEETS} and the qualified form is needed to reach the
 * others. Lookup ignores case.
 */
public final class BuiltinAssets {

  private BuiltinAssets() {
  }

  /**
   * The bundled sheets, in lookup order. When the same bare name exists on
   * several sheets, the one listed first wins.
   */
  private static final String[] SHEETS = { "platformer", "jumper", "space_shooter", "tappy_plane" };

  private static final String BASE = "images/";

  /** Where a single bundled sprite lives on its sheet. */
  public static final class Entry {
    public final String name;
    public final String sheet;
    public final String sheetPath;
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    private Entry(String name, String sheet, int x, int y, int width, int height) {
      this.name = name;
      this.sheet = sheet;
      this.sheetPath = BASE + sheet + ".png";
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
  }

  private static Map<String, Entry> assets;

  private static synchronized Map<String, Entry> getAssets() {
    if (assets != null) {
      return assets;
    }

    Map<String, Entry> loaded = new LinkedHashMap<>();
    XmlMapper mapper = (XmlMapper) new XmlMapper()
        .rebuild()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    for (String sheet : SHEETS) {
      String atlasPath = BASE + sheet + ".xml";
      try (InputStream in = openResource(atlasPath)) {
        if (in == null) {
          continue;
        }
        TextureAtlas atlas = mapper.readValue(in, TextureAtlas.class);
        if (atlas.subTextures == null) {
          continue;
        }
        for (SubTexture sub : atlas.subTextures) {
          if (sub.name == null) {
            continue;
          }
          String simpleName = Suggestions.stripExtension(sub.name);
          String qualified = key(sheet + "/" + simpleName);
          // A few sprites are listed twice in the same atlas; keep the first.
          if (loaded.containsKey(qualified)) {
            continue;
          }
          Entry entry = new Entry(simpleName, sheet, sub.x, sub.y, sub.width, sub.height);
          // The qualified name is always unique, the bare one is first-come.
          loaded.put(qualified, entry);
          loaded.putIfAbsent(key(simpleName), entry);
        }
      } catch (Exception e) {
        // A broken or missing atlas must never stop a student's program from
        // starting - it only means these built-in sprites are unavailable.
      }
    }

    assets = loaded;
    return assets;
  }

  private static InputStream openResource(String path) {
    ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    if (contextLoader != null) {
      InputStream in = contextLoader.getResourceAsStream(path);
      if (in != null) {
        return in;
      }
    }
    return BuiltinAssets.class.getClassLoader().getResourceAsStream(path);
  }

  private static String key(String name) {
    return Suggestions.normalise(name);
  }

  /**
   * Checks whether a string looks like a bundled sprite name rather than a file
   * path. Anything containing a path separator or a file extension is treated as
   * a path, so existing code that passes real paths keeps working.
   *
   * @param name the string to test
   * @return true if the string could name a bundled sprite
   */
  public static boolean isBuiltinName(String name) {
    if (name == null || name.isEmpty()) {
      return false;
    }
    if (name.indexOf('\\') >= 0 || name.startsWith("~") || name.startsWith("/")) {
      return false;
    }
    // "platformer/grass" is qualified, "sprites/cat.png" is a path - the
    // difference is the file extension.
    String last = name.substring(name.lastIndexOf('/') + 1);
    return last.indexOf('.') < 0;
  }

  /**
   * Looks up a bundled sprite.
   *
   * @param name a bare ("grass") or sheet-qualified ("platformer/grass") name
   * @return the entry, or null if no bundled sprite has that name
   */
  public static Entry get(String name) {
    if (name == null) {
      return null;
    }
    return getAssets().get(key(name));
  }

  /**
   * Checks whether a bundled sprite with this name exists.
   *
   * @param name a bare or sheet-qualified name
   * @return true if it exists
   */
  public static boolean contains(String name) {
    return get(name) != null;
  }

  /**
   * Returns every bundled sprite name, without the sheet qualifier and without
   * duplicates, in sheet order.
   *
   * @return all available names
   */
  public static List<String> getNames() {
    List<String> names = new ArrayList<>();
    for (Entry entry : getAssets().values()) {
      if (!names.contains(entry.name)) {
        names.add(entry.name);
      }
    }
    return names;
  }

  /**
   * Returns the bundled sheets, in lookup order.
   *
   * @return the sheet names
   */
  public static List<String> getSheets() {
    return List.of(SHEETS);
  }

  /**
   * Returns every bundled sprite, including the ones whose bare name is taken by
   * an earlier sheet, in sheet order.
   *
   * @return all sprites
   */
  public static List<Entry> getEntries() {
    // Each sprite is registered under its qualified and possibly its bare name,
    // so the same entry shows up twice; a set keyed on identity removes that.
    return new ArrayList<>(new LinkedHashSet<>(getAssets().values()));
  }

  /**
   * Returns the name to write in code for a sprite. That is its bare name,
   * unless an earlier sheet already claims it, in which case the sheet has to be
   * given as well.
   *
   * @param entry a bundled sprite
   * @return the name that resolves to exactly this sprite
   */
  public static String getReferenceName(Entry entry) {
    Entry bare = get(entry.name);
    return bare == entry ? entry.name : entry.sheet + "/" + entry.name;
  }

  /**
   * Finds bundled sprite names close to what was typed, so a wrong name can be
   * answered with "did you mean ...?".
   *
   * @param name  the name that could not be found
   * @param limit how many suggestions to return at most
   * @return the closest matching names, best first
   */
  public static List<String> suggest(String name, int limit) {
    return Suggestions.closestTo(getNames(), name, limit);
  }

  /** Root element of a Kenney texture atlas. */
  static class TextureAtlas {
    @JacksonXmlProperty(localName = "SubTexture")
    @JacksonXmlElementWrapper(useWrapping = false)
    public SubTexture[] subTextures;
  }

  /** One sprite inside a texture atlas. */
  static class SubTexture {
    @JacksonXmlProperty(isAttribute = true)
    public String name;

    @JacksonXmlProperty(isAttribute = true)
    public int x;

    @JacksonXmlProperty(isAttribute = true)
    public int y;

    @JacksonXmlProperty(isAttribute = true)
    public int width;

    @JacksonXmlProperty(isAttribute = true)
    public int height;
  }
}
