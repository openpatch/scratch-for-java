package docs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fills the interactive examples on the pages under {@code docs/book/examples}
 * from the demos under {@code src/examples/java/demos}, so a demo is written once
 * and the page shows that demo rather than a copy of it that drifts.
 *
 * <p>
 * A page opts in with a comment naming its demo folder on the line above its
 * Online IDE block:
 *
 * <pre>
 * &lt;!-- demo: smartRocket --&gt;
 * :::onlineide{height="560px" libraries="scratch"}
 * ...
 * :::
 * </pre>
 *
 * The opening line of the block is the page's own - its height and speed are a
 * matter of taste - and everything between it and the closing {@code :::} is
 * generated: one {@code @file} line per asset of the demo, then the demo as a
 * single program.
 *
 * <p>
 * The Online IDE has no packages and no imports, and the program is a
 * {@code void main()} beside the classes, the Java 25 form a reader can also copy
 * into a file and run. So the demo loses its package and imports, the
 * {@code public static void main} of its entry class moves out to become that
 * {@code void main()}, and the {@code demos/<folder>/} in front of its asset paths
 * goes, because the browser puts the assets next to the program. Nothing here
 * parses Java; the demos share one shape, and {@code DocumentationSnippetsTest}
 * compiles what comes out.
 *
 * <p>
 * The assets are copied to {@code docs/public/examples/<page>}, where the
 * {@code @file} lines point. The {@code generate-demo-assets} execution in the
 * pom does only that, on every build. Rewriting the pages is left to
 * {@code ./scripts/update-demo-pages.sh}, which passes {@code --write}: a build
 * should not edit files that are committed, and {@code DemoPagesTest} fails when
 * a page has fallen behind its demo.
 */
public final class GenerateDemoPages {

  static final Path PAGES = Path.of("docs/book/examples");
  static final Path DEMOS = Path.of("src/examples/java/demos");
  private static final Path PUBLIC = Path.of("docs/public/examples");

  private static final Pattern MARKER =
      Pattern.compile("<!-- demo: (\\w+) -->\\n(:::onlineide\\b[^\\n]*\\n)(.*?\\n)(:::)(?=\\n|$)",
          Pattern.DOTALL);

  private static final Pattern TYPE_DECLARATION = Pattern.compile(
      "^(?:(?:public|private|protected|static|abstract|final)\\s+)*"
          + "(?:class|interface|enum|record)\\s+(\\w+)(?:\\s+extends\\s+(\\w+))?");

  private static final Pattern MAIN_METHOD =
      Pattern.compile("^\\s*public static void main\\s*\\(.*\\)\\s*\\{\\s*$");

  private GenerateDemoPages() {
  }

  public static void main(String[] args) throws IOException {
    boolean write = List.of(args).contains("--write");

    int pages = 0;
    for (Path page : pages()) {
      String text = Files.readString(page);
      Map<String, String> demos = demosOn(text);
      if (demos.isEmpty()) {
        continue;
      }
      pages++;
      String name = pageName(page);
      for (String demo : demos.keySet()) {
        copyAssets(demo, name);
      }
      if (write) {
        String generated = render(page, text);
        if (!generated.equals(text)) {
          Files.writeString(page, generated);
          System.out.println("Updated " + page);
        }
      }
    }
    System.out.println("Copied the assets of " + pages + " demo pages to " + PUBLIC);
  }

  /** Every page under docs/book/examples, in a stable order. */
  static List<Path> pages() throws IOException {
    try (Stream<Path> list = Files.list(PAGES)) {
      return list.filter(p -> p.toString().endsWith(".md")).sorted().collect(Collectors.toList());
    }
  }

  /** The demo folders a page names, each with the Online IDE block it fills. */
  static Map<String, String> demosOn(String text) {
    var demos = new LinkedHashMap<String, String>();
    Matcher marker = MARKER.matcher(text);
    while (marker.find()) {
      demos.put(marker.group(1), marker.group(3));
    }
    return demos;
  }

  /** The page with every marked Online IDE block filled from its demo. */
  static String render(Path page, String text) throws IOException {
    String name = pageName(page);
    Matcher marker = MARKER.matcher(text);
    var out = new StringBuilder();
    while (marker.find()) {
      String body = block(marker.group(1), name);
      marker.appendReplacement(out, Matcher.quoteReplacement(
          "<!-- demo: " + marker.group(1) + " -->\n" + marker.group(2) + body + marker.group(4)));
    }
    marker.appendTail(out);
    return out.toString();
  }

  private static String pageName(Path page) {
    String file = page.getFileName().toString();
    return file.substring(0, file.length() - ".md".length());
  }

  // ---------------------------------------------------------------- the block

  /** What goes between the opening line of the Online IDE block and its end. */
  static String block(String demo, String page) throws IOException {
    Path folder = DEMOS.resolve(demo);
    var out = new StringBuilder("\n");

    List<String> assets = assets(folder);
    for (String asset : assets) {
      out.append("@file dest=\"").append(asset).append("\" src=\"/examples/")
          .append(page).append("/").append(asset).append("\"\n");
    }
    if (!assets.isEmpty()) {
      out.append("\n");
    }

    var sources = new ArrayList<Source>();
    try (Stream<Path> list = Files.list(folder)) {
      for (Path file : list.filter(p -> p.toString().endsWith(".java")).sorted().toList()) {
        sources.add(Source.of(file, demo));
      }
    }
    Source entry = sources.stream().filter(s -> s.main != null).findFirst()
        .orElseThrow(() -> new IOException(folder + " has no main method"));

    out.append("```java ").append(entry.file).append("\n\n");
    out.append("void main() {\n");
    for (String line : entry.main) {
      out.append(line.isBlank() ? "" : "  " + line).append("\n");
    }
    out.append("}\n");
    for (Source source : inReadingOrder(entry, sources)) {
      out.append("\n").append(source.types).append("\n");
    }
    out.append("```\n\n");
    return out.toString();
  }

  /**
   * The order someone reading from the top would want: the entry file, then the
   * windows and stages, then everything else, each group in the order the program
   * first mentions it. A class that extends another one of the demo's classes
   * waits for that one, so a base class is read before what builds on it.
   */
  private static List<Source> inReadingOrder(Source entry, List<Source> sources) {
    var ordered = new ArrayList<Source>();
    var left = new ArrayList<>(sources);
    left.remove(entry);
    ordered.add(entry);
    var shown = new StringBuilder(entry.types);
    while (!left.isEmpty()) {
      Source next = left.stream()
          .min(Comparator.comparing((Source s) -> !s.isStage())
              .thenComparingInt(s -> s.firstMentionIn(shown.toString()))
              .thenComparing(s -> s.file))
          .get();
      for (Source base = next.baseIn(left); base != null; base = base.baseIn(left)) {
        next = base;
      }
      left.remove(next);
      ordered.add(next);
      shown.append("\n").append(next.types);
    }
    return ordered;
  }

  /** The demo's committed assets, relative to its folder, numbers in number order. */
  static List<String> assets(Path folder) throws IOException {
    try (Stream<Path> walk = Files.walk(folder)) {
      return walk.filter(Files::isRegularFile)
          .map(p -> folder.relativize(p).toString().replace('\\', '/'))
          .filter(GenerateDemoPages::isAsset)
          .sorted(GenerateDemoPages::naturally)
          .collect(Collectors.toList());
    }
  }

  /**
   * Images, sounds and fonts the program loads. Sources, BlueJ's leftovers, the
   * SVGs the PNGs were drawn from and licence texts stay behind.
   */
  private static boolean isAsset(String path) {
    String lower = path.toLowerCase();
    return lower.matches(".*\\.(png|jpe?g|gif|ogg|wav|mp3|aiff?|au|ttf|otf|json|tmx|tsx|txt|csv)")
        && !lower.endsWith("license.txt");
  }

  /** "Idle (2).png" before "Idle (10).png". */
  private static int naturally(String a, String b) {
    var number = Pattern.compile("\\d+");
    Matcher ma = number.matcher(a);
    Matcher mb = number.matcher(b);
    int ia = 0;
    int ib = 0;
    while (ma.find(ia) && mb.find(ib) && ma.start() - ia == mb.start() - ib
        && a.substring(ia, ma.start()).equals(b.substring(ib, mb.start()))) {
      int byValue = Long.compare(Long.parseLong(ma.group()), Long.parseLong(mb.group()));
      if (byValue != 0) {
        return byValue;
      }
      ia = ma.end();
      ib = mb.end();
    }
    return a.substring(ia).compareTo(b.substring(ib));
  }

  // --------------------------------------------------------------- the source

  private static void copyAssets(String demo, String page) throws IOException {
    Path folder = DEMOS.resolve(demo);
    Path target = PUBLIC.resolve(page);
    for (String asset : assets(folder)) {
      Path destination = target.resolve(asset);
      Files.createDirectories(destination.getParent());
      Files.copy(folder.resolve(asset), destination, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /** One demo file, taken apart into its classes and, for the entry file, its main. */
  private static final class Source {
    final String file;
    final List<String> typeNames = new ArrayList<>();
    final List<String> bases = new ArrayList<>();
    String types;
    List<String> main;

    private Source(String file) {
      this.file = file;
    }

    static Source of(Path path, String demo) throws IOException {
      var source = new Source(path.getFileName().toString());
      var kept = new ArrayList<String>();
      List<String> main = null;
      int mainDepth = -1;
      int depth = 0;
      boolean mainWasFirst = false;

      for (String raw : Files.readString(path).split("\n", -1)) {
        String line = raw.stripTrailing().replace("\"demos/" + demo + "/", "\"");
        int before = depth;
        depth += count(line, '{') - count(line, '}');

        if (mainDepth >= 0) {
          if (depth <= mainDepth) {
            mainDepth = -1;
            mainWasFirst = !kept.isEmpty() && kept.get(kept.size() - 1).endsWith("{");
          } else {
            main.add(dedent(line, 2));
          }
          continue;
        }
        if (mainWasFirst) {
          // main opened the class; the blank line after it would now open it instead
          mainWasFirst = false;
          if (line.isBlank()) {
            continue;
          }
        }
        if (before == 0 && (line.startsWith("package ") || line.startsWith("import "))) {
          continue;
        }
        if (before == 1 && MAIN_METHOD.matcher(line).matches()) {
          main = new ArrayList<>();
          mainDepth = before;
          // the blank line that separated main from what came before goes with it
          if (!kept.isEmpty() && kept.get(kept.size() - 1).isBlank()) {
            kept.remove(kept.size() - 1);
          }
          continue;
        }
        if (before == 0) {
          Matcher type = TYPE_DECLARATION.matcher(line);
          if (type.find()) {
            source.typeNames.add(type.group(1));
            if (type.group(2) != null) {
              source.bases.add(type.group(2));
            }
            line = line.replaceFirst("^public\\s+", "");
          }
        }
        kept.add(line);
      }

      source.main = main;
      source.types = String.join("\n", kept).strip();
      return source;
    }

    /** Whether the file holds a window or a stage, which the program is made of. */
    boolean isStage() {
      return bases.stream().anyMatch(b -> b.endsWith("Stage") || b.endsWith("Window"));
    }

    /** The file among these that declares a class this one extends, if any. */
    Source baseIn(List<Source> sources) {
      for (Source other : sources) {
        if (other != this && bases.stream().anyMatch(other.typeNames::contains)) {
          return other;
        }
      }
      return null;
    }

    int firstMentionIn(String text) {
      int first = Integer.MAX_VALUE;
      for (String name : typeNames) {
        Matcher mention = Pattern.compile("\\b" + name + "\\b").matcher(text);
        if (mention.find()) {
          first = Math.min(first, mention.start());
        }
      }
      return first;
    }
  }

  private static String dedent(String line, int levels) {
    String indent = "  ".repeat(levels);
    return line.startsWith(indent) ? line.substring(indent.length()) : line.strip();
  }

  private static int count(String line, char c) {
    int n = 0;
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == c) {
        n++;
      }
    }
    return n;
  }
}
