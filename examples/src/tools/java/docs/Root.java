package docs;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The repository's root: the folder with {@code docs/book}, found upwards from the working
 * directory, so the tools work from the root ({@code mvn exec:java}) and from the examples module
 * (surefire runs the tests there).
 */
public final class Root {

  public static final Path ROOT = find();

  private Root() {}

  private static Path find() {
    for (Path dir = Path.of("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
      if (Files.isDirectory(dir.resolve("docs/book"))) {
        return dir;
      }
    }
    return Path.of("").toAbsolutePath();
  }
}
