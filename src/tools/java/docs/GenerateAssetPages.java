package docs;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openpatch.scratch.internal.BuiltinAssets;
import org.openpatch.scratch.internal.BuiltinSounds;

/**
 * Generates the two documentation pages that list every asset bundled with
 * Scratch for Java, straight from the resources themselves.
 *
 * <p>
 * The sprite page draws each sprite by shifting the sheet behind a window of
 * the right size, the same way the library slices costumes out of an atlas.
 * That keeps the page to four images instead of 838, and it means a change to a
 * sheet shows up in the docs without anything being re-exported.
 *
 * <p>
 * Run through the {@code generate-asset-pages} execution in the pom, or
 * directly with {@code mvn exec:java -Dexec.mainClass=docs.GenerateAssetPages}.
 */
public final class GenerateAssetPages {

  /** Edge length of the box a sprite preview is scaled to fit into. */
  private static final int TILE = 88;

  private static final Path RESOURCES = Path.of("src/main/resources");
  private static final Path BOOK = Path.of("docs/book");
  private static final Path PUBLIC = Path.of("docs/public/builtin");

  public static void main(String[] args) throws IOException {
    Files.createDirectories(BOOK);
    Files.createDirectories(PUBLIC);

    copySheets();
    copySounds();

    Files.writeString(BOOK.resolve("sprites.md"), spritePage());
    Files.writeString(BOOK.resolve("sounds.md"), soundPage());

    System.out.println("Generated docs/book/sprites.md with "
        + BuiltinAssets.getEntries().size() + " sprites");
    System.out.println("Generated docs/book/sounds.md with "
        + BuiltinSounds.getNames().size() + " sounds");
  }

  // ---------------------------------------------------------------- assets

  private static void copySheets() throws IOException {
    for (String sheet : BuiltinAssets.getSheets()) {
      Path source = RESOURCES.resolve("images/" + sheet + ".png");
      if (Files.exists(source)) {
        Files.copy(source, PUBLIC.resolve(sheet + ".png"), StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }

  private static void copySounds() throws IOException {
    Path target = PUBLIC.resolve("sounds");
    for (String name : BuiltinSounds.getNames()) {
      String resource = BuiltinSounds.get(name);
      Path source = RESOURCES.resolve(resource);
      if (!Files.exists(source)) {
        continue;
      }
      // "sounds/impact/x.ogg" keeps its folders below docs/public/builtin.
      Path destination = target.resolve(resource.substring("sounds/".length()));
      Files.createDirectories(destination.getParent());
      Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  // ---------------------------------------------------------------- sprites

  private static String spritePage() {
    StringBuilder out = new StringBuilder();
    out.append("""
        ---
        name: Sprites
        index: 5
        styles:
          - /builtin.css
        scripts:
          - /builtin.js
        ---

        # Built-in Sprites

        These sprites are built into Scratch for Java, so you can use them
        without downloading anything. Add one to a sprite by its name:

        ```java
        this.addCostume("bunny1_jump");
        ```

        Click a sprite to copy that line.

        :::alert{info}

        The artwork is made by [Kenney](https://kenney.nl) and is released under
        CC0, which means you may use it for anything, also without saying where
        it came from.

        :::

        """);

    List<BuiltinAssets.Entry> entries = BuiltinAssets.getEntries();
    out.append(filterBar("sprite", entries.size()));

    Map<String, List<BuiltinAssets.Entry>> bySheet = new LinkedHashMap<>();
    for (String sheet : BuiltinAssets.getSheets()) {
      bySheet.put(sheet, new ArrayList<>());
    }
    for (BuiltinAssets.Entry entry : entries) {
      bySheet.computeIfAbsent(entry.sheet, s -> new ArrayList<>()).add(entry);
    }

    for (Map.Entry<String, List<BuiltinAssets.Entry>> group : bySheet.entrySet()) {
      if (group.getValue().isEmpty()) {
        continue;
      }
      out.append("<section class=\"ba-group\" data-group=\"")
          .append(escape(group.getKey()))
          .append("\">\n<h2>")
          .append(escape(title(group.getKey())))
          .append("</h2>\n<div class=\"ba-grid\">\n");

      for (BuiltinAssets.Entry entry : group.getValue()) {
        out.append(tile(entry));
      }

      out.append("</div>\n</section>\n\n");
    }

    out.append("<p class=\"ba-empty\" hidden>No sprite matches that name.</p>\n");
    return out.toString();
  }

  private static String tile(BuiltinAssets.Entry entry) {
    String reference = BuiltinAssets.getReferenceName(entry);

    // Most sprites sit in a cell with room to spare - a walking pose in a cell
    // sized for a jumping one, say. Previewing the cell would show them small
    // and at wildly different sizes, so the preview uses the part that is
    // actually painted. The name still refers to the whole sprite.
    int[] box = Sheets.contentBox(entry);
    int x = entry.x + box[0];
    int y = entry.y + box[1];
    int width = box[2];
    int height = box[3];

    // Shrink anything larger than the tile, but never blow small sprites up:
    // seeing that a coin is tiny next to a player is worth keeping.
    double scale = Math.min(1.0, (double) TILE / Math.max(width, height));

    return "<button class=\"ba-tile\" data-name=\"" + escape(reference.toLowerCase()) + "\""
        + " data-copy=\"" + escape("this.addCostume(\"" + reference + "\");") + "\""
        + " title=\"" + escape(reference + "  (" + entry.width + "x" + entry.height + ")") + "\">"
        + "<span class=\"ba-frame\">"
        + "<span class=\"ba-sprite ba-sheet-" + escape(entry.sheet) + "\" style=\""
        + "width:" + width + "px;"
        + "height:" + height + "px;"
        + "background-position:-" + x + "px -" + y + "px;"
        + "transform:scale(" + round(scale) + ")\"></span>"
        + "</span>"
        + "<span class=\"ba-name\">" + escape(reference) + "</span>"
        + "</button>\n";
  }

  // ----------------------------------------------------------------- sounds

  private static String soundPage() {
    StringBuilder out = new StringBuilder();
    out.append("""
        ---
        name: Sounds
        index: 6
        styles:
          - /builtin.css
        scripts:
          - /builtin.js
        ---

        # Built-in Sounds

        These sounds are built into Scratch for Java, so you can use them
        without downloading anything. Add one to a sprite by its name:

        ```java
        this.addSound("footstep_carpet_000");
        this.playSound("footstep_carpet_000");
        ```

        Press play to listen, or click the name to copy the line above.

        :::alert{info}

        The sounds are made by [Kenney](https://kenney.nl) and are released under
        CC0, which means you may use them for anything, also without saying where
        they came from.

        :::

        """);

    List<String> names = BuiltinSounds.getNames();
    out.append(filterBar("sound", names.size()));

    Map<String, List<String>> byFolder = new LinkedHashMap<>();
    for (String name : names) {
      String resource = BuiltinSounds.get(name);
      String folder = resource.substring("sounds/".length(), resource.lastIndexOf('/'));
      byFolder.computeIfAbsent(folder, f -> new ArrayList<>()).add(name);
    }

    for (Map.Entry<String, List<String>> group : byFolder.entrySet()) {
      out.append("<section class=\"ba-group\" data-group=\"")
          .append(escape(group.getKey()))
          .append("\">\n<h2>")
          .append(escape(title(group.getKey())))
          .append("</h2>\n<div class=\"bs-list\">\n");

      for (String name : group.getValue()) {
        out.append(row(name, BuiltinSounds.get(name)));
      }

      out.append("</div>\n</section>\n\n");
    }

    out.append("<p class=\"ba-empty\" hidden>No sound matches that name.</p>\n");
    return out.toString();
  }

  private static String row(String name, String resource) {
    String url = "/builtin/sounds/" + encodePath(resource.substring("sounds/".length()));

    return "<div class=\"bs-row\" data-name=\"" + escape(name.toLowerCase()) + "\">"
        + "<button class=\"bs-play\" data-src=\"" + escape(url) + "\""
        + " aria-label=\"" + escape("Play " + name) + "\"></button>"
        + "<button class=\"bs-name\" data-copy=\""
        + escape("this.addSound(\"" + name + "\");") + "\">"
        + escape(name) + "</button>"
        + "</div>\n";
  }

  // ------------------------------------------------------------------ bits

  private static String filterBar(String what, int total) {
    return "<div class=\"ba-filter\">\n"
        + "<input type=\"search\" class=\"ba-search\" placeholder=\"Search "
        + total + " " + what + "s...\" aria-label=\"Search " + what + "s\" />\n"
        + "<span class=\"ba-count\" data-total=\"" + total + "\">" + total + " " + what + "s</span>\n"
        + "</div>\n\n";
  }

  /** Turns "space_shooter" and "8-Bit jingles" into a readable heading. */
  private static String title(String raw) {
    String spaced = raw.replace('_', ' ').replace('/', ' ');
    return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
  }

  private static String encodePath(String path) {
    List<String> parts = new ArrayList<>();
    for (String segment : path.split("/")) {
      parts.add(URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"));
    }
    return String.join("/", parts);
  }

  private static String round(double value) {
    return String.valueOf(Math.round(value * 1000.0) / 1000.0);
  }

  private static String escape(String raw) {
    return raw.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;");
  }

  private GenerateAssetPages() {
  }
}
