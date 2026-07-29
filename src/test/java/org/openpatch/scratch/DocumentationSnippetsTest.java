package org.openpatch.scratch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
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
 * Compiles every Java example in the hand-written documentation.
 *
 * <p>
 * The tutorials are the first thing a beginner types, so an example that no
 * longer compiles is worse than no example at all. Two of them had in fact been
 * broken since {@code KeyCode} became an enum, and nothing noticed until they
 * were compiled by hand. This test does that on every build instead.
 *
 * <p>
 * Only whole classes are checked; fragments meant to be read rather than run are
 * skipped. Pages are compiled a page at a time, so classes on the same page can
 * refer to each other, and where a page builds a class up in steps only the last
 * version of it is used.
 */
class DocumentationSnippetsTest {

  private static final Pattern JAVA_BLOCK = Pattern.compile("```java\\n(.*?)```", Pattern.DOTALL);
  private static final Pattern CLASS_NAME = Pattern.compile("(?:public\\s+)?class\\s+(\\w+)");

  @Test
  void everyDocumentationExampleCompiles() throws IOException {
    Path docs = Path.of("docs", "book");
    if (!Files.isDirectory(docs)) {
      return; // documentation is not part of every checkout
    }

    List<Path> pages;
    try (Stream<Path> walk = Files.walk(docs)) {
      pages = walk
          .filter(p -> p.toString().endsWith(".md"))
          .filter(p -> !p.toString().contains("reference"))
          .filter(p -> !p.getFileName().toString().equals("changelog.md"))
          .sorted()
          .collect(Collectors.toList());
    }

    var failures = new ArrayList<String>();
    var checked = 0;

    for (Path page : pages) {
      Map<String, String> classes = classesOn(page);
      if (classes.isEmpty()) {
        continue;
      }
      checked += classes.size();
      String problem = compile(classes);
      if (problem != null) {
        failures.add(page + "\n" + problem);
      }
    }

    assertTrue(checked > 0, "no documentation examples were found to check");
    assertTrue(failures.isEmpty(),
        "documentation examples do not compile:\n\n" + String.join("\n\n", failures));
  }

  /** The last version of each class defined on a page, keyed by class name. */
  private Map<String, String> classesOn(Path page) throws IOException {
    var found = new LinkedHashMap<String, String>();
    String text = Files.readString(page);
    Matcher blocks = JAVA_BLOCK.matcher(text);
    while (blocks.find()) {
      String block = blocks.group(1);
      Matcher names = CLASS_NAME.matcher(block);
      while (names.find()) {
        // a later block showing the finished class replaces the earlier sketch
        found.put(names.group(1), block);
      }
    }
    return found;
  }

  private String compile(Map<String, String> classes) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      return null; // no compiler on this JVM, nothing to say
    }

    Path dir = Files.createTempDirectory("scratch-docs-");
    var files = new ArrayList<File>();
    var written = new java.util.HashSet<String>();

    for (Map.Entry<String, String> entry : classes.entrySet()) {
      String block = entry.getValue();
      // one file per block: a block may hold several classes, and the first
      // one names the file
      String first = firstClassIn(block);
      if (first == null || !written.add(first)) {
        continue;
      }
      String body = block.contains("import org.openpatch")
          ? block
          : "import org.openpatch.scratch.*;\n" + block;
      body = body.replaceAll("\\bpublic class\\b", "class");
      Path file = dir.resolve(first + ".java");
      Files.writeString(file, body);
      files.add(file.toFile());
    }
    if (files.isEmpty()) {
      return null;
    }
    return compile(dir, files);
  }

  /**
   * Compiles one whole program, in one file, as the page shows it - which for an
   * interactive example means a compact source file.
   */
  private String compileProgram(String name, String source) throws IOException {
    if (ToolProvider.getSystemJavaCompiler() == null) {
      return null; // no compiler on this JVM, nothing to say
    }
    Path dir = Files.createTempDirectory("scratch-docs-");
    Path file = dir.resolve(name + ".java");
    Files.writeString(file, source);
    return compile(dir, List.of(file.toFile()));
  }

  private String compile(Path dir, List<File> files) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    var diagnostics = new DiagnosticCollector<JavaFileObject>();
    try (StandardJavaFileManager manager =
        compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
      var options = List.of("-d", dir.toString(), "-nowarn",
          "-classpath", System.getProperty("java.class.path"));
      boolean ok = compiler
          .getTask(null, manager, diagnostics, options, null,
              manager.getJavaFileObjectsFromFiles(files))
          .call();
      if (ok) {
        return null;
      }
    }
    return diagnostics.getDiagnostics().stream()
        .filter(d -> d.getKind() == javax.tools.Diagnostic.Kind.ERROR)
        .map(d -> "    " + d.getSource().getName().replaceAll(".*/", "")
            + ":" + d.getLineNumber() + " " + d.getMessage(Locale.ENGLISH))
        .limit(6)
        .collect(Collectors.joining("\n"));
  }

  private String firstClassIn(String block) {
    Matcher m = CLASS_NAME.matcher(block);
    return m.find() ? m.group(1) : null;
  }

  // ---------------------------------------------------------------------------
  // Interactive examples
  // ---------------------------------------------------------------------------

  /** A ```java block carrying a file name, which is what a :::onlineide block holds. */
  private static final Pattern ONLINE_BLOCK =
      Pattern.compile("```java +\\S+\\n(.*?)```", Pattern.DOTALL);
  private static final Pattern ONLINE_DIRECTIVE =
      Pattern.compile(":::onlineide\\b.*?\\n(.*?)\\n:::", Pattern.DOTALL);

  /**
   * Compiles the interactive examples — the ones in {@code :::onlineide} blocks,
   * which readers run in the browser rather than in an editor.
   *
   * <p>
   * They are written for the Online IDE, which has no packages and no imports,
   * so the imports are put back. Everything else is compiled as it is shown: a
   * {@code void main()} beside the classes is a compact source file, a whole
   * Java program, so a reader can paste the example into a file of their own and
   * run it. That is the thing worth checking - an interactive example that only
   * works in the browser would teach the wrong Java.
   */
  @Test
  void everyInteractiveExampleCompiles() throws IOException {
    Path docs = Path.of("docs", "book");
    if (!Files.isDirectory(docs)) {
      return; // documentation is not part of every checkout
    }
    // Compact source files are Java 25; an older compiler cannot judge them.
    assumeTrue(Runtime.version().feature() >= 25,
        "the interactive examples are Java 25 programs; this JDK is "
            + Runtime.version().feature());

    List<Path> pages;
    try (Stream<Path> walk = Files.walk(docs)) {
      pages = walk
          .filter(p -> p.toString().endsWith(".md"))
          .filter(p -> !p.getFileName().toString().equals("changelog.md"))
          .sorted()
          .collect(Collectors.toList());
    }

    var failures = new ArrayList<String>();
    var checked = 0;

    for (Path page : pages) {
      String text = Files.readString(page);
      Matcher directives = ONLINE_DIRECTIVE.matcher(text);
      int index = 0;
      while (directives.find()) {
        Matcher blocks = ONLINE_BLOCK.matcher(directives.group(1));
        while (blocks.find()) {
          index++;
          checked++;
          String problem = compileProgram("Block" + index, asCompilationUnit(blocks.group(1)));
          if (problem != null) {
            failures.add(page + " (interactive example " + index + ")\n" + problem);
          }
        }
      }
    }

    assertTrue(checked > 0, "no interactive examples were found to check");
    assertTrue(failures.isEmpty(),
        "interactive examples do not compile:\n\n" + String.join("\n\n", failures));
  }

  /**
   * Puts back the one thing the Online IDE does not need, and the desktop does:
   * the import. The program itself is left exactly as the page shows it.
   */
  private String asCompilationUnit(String block) {
    return "import org.openpatch.scratch.*;\n" + block;
  }
}
