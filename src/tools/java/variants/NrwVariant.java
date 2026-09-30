package variants;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Writes the sources of the NRW build of the library.
 *
 * <p>
 * The NRW build is the same library, except that the methods handing out a list
 * of sprites hand out the {@code List} of the NRW Zentralabitur instead of
 * {@code java.util.List}. Java cannot overload on the return type, so both
 * versions of such a method are written into the one source file:
 *
 * <pre>
 *   // nrw-standard-begin
 *   public List&lt;Sprite&gt; getAll() { ... }
 *   // nrw-standard-end
 *   // nrw: public &lt;L&gt; L getAll() { ... }
 * </pre>
 *
 * The normal build compiles the file as it is, where the NRW version is a
 * comment. This copies the sources and swaps the two: the standard version is
 * dropped and the {@code // nrw:} prefix removed.
 *
 * <p>
 * It runs in the {@code nrw} profile of the pom, before compiling, through the
 * java launcher, since nothing has been compiled at that point:
 * {@code java src/tools/java/variants/NrwVariant.java src/main/java target/nrw-sources}.
 */
public final class NrwVariant {

  private static final String BEGIN = "// nrw-standard-begin";
  private static final String END = "// nrw-standard-end";
  private static final Pattern NRW_LINE = Pattern.compile("^(\\s*)// nrw:(?: (.*))?$");

  private NrwVariant() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Usage: java NrwVariant.java <source directory> <target directory>");
      System.exit(2);
    }
    write(Path.of(args[0]), Path.of(args[1]));
  }

  /**
   * Copies every file below {@code from} to {@code to}, rewriting the Java
   * sources into their NRW version. Whatever was in {@code to} is removed first.
   */
  public static void write(Path from, Path to) throws IOException {
    if (Files.exists(to)) {
      try (Stream<Path> walk = Files.walk(to)) {
        for (Path p : (Iterable<Path>) walk.sorted((a, b) -> b.compareTo(a))::iterator) {
          Files.delete(p);
        }
      }
    }
    try (Stream<Path> walk = Files.walk(from)) {
      walk.filter(Files::isRegularFile).forEach(file -> {
        Path target = to.resolve(from.relativize(file).toString());
        try {
          Files.createDirectories(target.getParent());
          if (file.toString().endsWith(".java")) {
            String source = Files.readString(file, StandardCharsets.UTF_8);
            Files.writeString(target, rewrite(source, file.toString()), StandardCharsets.UTF_8);
          } else {
            Files.copy(file, target);
          }
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      });
    }
  }

  /**
   * Returns the NRW version of one source file.
   *
   * @param source the file's content
   * @param name   the file's name, for the error messages
   */
  public static String rewrite(String source, String name) {
    List<String> out = new ArrayList<>();
    boolean inStandard = false;
    int lineNumber = 0;
    for (String line : source.split("\n", -1)) {
      lineNumber++;
      String trimmed = line.trim();
      if (trimmed.equals(BEGIN)) {
        if (inStandard) {
          throw new IllegalStateException(name + ":" + lineNumber + ": nested " + BEGIN);
        }
        inStandard = true;
        continue;
      }
      if (trimmed.equals(END)) {
        if (!inStandard) {
          throw new IllegalStateException(name + ":" + lineNumber + ": " + END + " without " + BEGIN);
        }
        inStandard = false;
        continue;
      }
      if (inStandard) {
        continue;
      }
      Matcher nrw = NRW_LINE.matcher(line);
      if (nrw.matches()) {
        out.add(nrw.group(2) == null ? "" : nrw.group(1) + nrw.group(2));
      } else {
        out.add(line);
      }
    }
    if (inStandard) {
      throw new IllegalStateException(name + ": " + BEGIN + " without " + END);
    }
    return String.join("\n", out);
  }
}
