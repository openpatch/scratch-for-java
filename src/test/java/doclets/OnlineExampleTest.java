package doclets;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;

/**
 * Compiles the interactive version of every reference example.
 *
 * <p>
 * The reference pages no longer show a recording of an example, they embed the
 * example itself, and what they embed is what {@link OnlineExample} makes of the
 * desktop source. That rewrite is text manipulation over a shape, so the way it
 * breaks is by producing a program that no longer compiles - an example rewritten
 * into nonsense would otherwise sit on a documentation page until a reader
 * pressed the run button and got a wall of errors.
 *
 * <p>
 * Compiling proves the rewrite kept the example a valid program. It cannot prove
 * the program still does something worth watching; that is what reading the page
 * is for.
 */
class OnlineExampleTest {

  private static final Path REFERENCE = Path.of("src", "examples", "java", "reference");

  private static final Pattern TYPE_DECLARATION = Pattern.compile(
      "^\\s*(?:(?:public|abstract|final|static)\\s+)*(?:class|interface|enum|record)\\s+\\w+");

  @Test
  void everyReferenceExampleStaysAProgram() throws IOException {
    if (!Files.isDirectory(REFERENCE)) {
      return; // the examples are not part of every checkout
    }

    var failures = new ArrayList<String>();
    var checked = 0;

    List<Path> entries;
    try (Stream<Path> list = Files.list(REFERENCE)) {
      entries = list.sorted().collect(Collectors.toList());
    }

    for (Path entry : entries) {
      String folder;
      List<String> files;

      if (Files.isDirectory(entry)) {
        if (entry.getFileName().toString().equals("assets")) {
          continue;
        }
        folder = entry.getFileName().toString();
        try (Stream<Path> list = Files.list(entry)) {
          files = list.map(p -> p.getFileName().toString())
              .filter(name -> name.endsWith(".java"))
              .sorted()
              .collect(Collectors.toList());
        }
      } else if (entry.toString().endsWith(".java")) {
        folder = "";
        files = List.of(entry.getFileName().toString());
      } else {
        continue;
      }

      String online = OnlineExample.of(folder, files);
      if (online == null) {
        continue; // needs something only a desktop program has
      }

      checked++;
      String name = folder.isEmpty()
          ? files.get(0).replace(".java", "")
          : folder;
      String problem = compile(asCompilationUnit(online));
      if (problem != null) {
        failures.add(name + "\n" + problem);
      }
    }

    assertTrue(checked > 0, "no reference examples were found to check");
    assertTrue(failures.isEmpty(),
        "reference examples do not survive the rewrite for the Online IDE:\n\n"
            + String.join("\n\n", failures));
  }

  /**
   * Puts back what the Online IDE does not need: the imports, and a {@code main}
   * around the statements that stand at the top of the program.
   */
  private String asCompilationUnit(String online) {
    var types = new StringBuilder();
    var statements = new StringBuilder();
    int depth = 0;
    boolean inType = false;

    for (String line : online.split("\n", -1)) {
      if (depth == 0 && !inType && TYPE_DECLARATION.matcher(line).find()) {
        inType = true;
      }
      (inType ? types : statements).append(line).append("\n");

      depth += count(line, '{') - count(line, '}');
      if (inType && depth == 0) {
        inType = false;
      }
    }

    // In the Online IDE every class is in scope without an import, which is
    // why the rewrite drops them. Putting the extensions back here is what
    // lets the same program be compiled against the desktop library.
    return "import org.openpatch.scratch.*;\n"
        + "import org.openpatch.scratch.extensions.camera.*;\n"
        + "import org.openpatch.scratch.extensions.fs.*;\n"
        + "import org.openpatch.scratch.extensions.pixels.*;\n"
        + "import org.openpatch.scratch.extensions.shader.*;\n"
        + "import org.openpatch.scratch.extensions.sorting.*;\n"
        + "import org.openpatch.scratch.extensions.tiled.*;\n"
        + types
        + "\nclass Example {\n  public static void main(String[] args) {\n"
        + statements
        + "\n  }\n}\n";
  }

  private String compile(String source) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      return null; // no compiler on this JVM, nothing to say
    }

    Path dir = Files.createTempDirectory("scratch-online-");
    Path file = dir.resolve("Example.java");
    Files.writeString(file, source);

    var diagnostics = new DiagnosticCollector<JavaFileObject>();
    try (StandardJavaFileManager manager =
        compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
      var options = List.of("-d", dir.toString(), "-nowarn",
          "-classpath", System.getProperty("java.class.path"));
      boolean ok = compiler
          .getTask(null, manager, diagnostics, options, null,
              manager.getJavaFileObjectsFromFiles(List.of(new File(file.toString()))))
          .call();
      if (ok) {
        return null;
      }
    }
    return diagnostics.getDiagnostics().stream()
        .filter(d -> d.getKind() == javax.tools.Diagnostic.Kind.ERROR)
        .map(d -> "    line " + d.getLineNumber() + " " + d.getMessage(Locale.ENGLISH))
        .limit(4)
        .collect(Collectors.joining("\n"));
  }

  private int count(String line, char c) {
    int n = 0;
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == c) {
        n++;
      }
    }
    return n;
  }
}
