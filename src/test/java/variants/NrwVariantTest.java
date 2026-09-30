package variants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NrwVariantTest {

  @Test
  void swapsTheStandardVersionForTheNrwOne() {
    String source = String.join("\n",
        "class A {",
        "  // nrw-standard-begin",
        "  public List<Sprite> getAll() {",
        "    return sprites;",
        "  }",
        "  // nrw-standard-end",
        "  // nrw: public <L> L getAll() {",
        "  // nrw:   return NrwList.of(sprites);",
        "  // nrw:",
        "  // nrw: }",
        "}");

    assertEquals(String.join("\n",
        "class A {",
        "  public <L> L getAll() {",
        "    return NrwList.of(sprites);",
        "",
        "  }",
        "}"), NrwVariant.rewrite(source, "A.java"));
  }

  @Test
  void leavesFilesWithoutMarkersAlone() {
    String source = "class A {\n  // a comment\n  void a() {}\n}\n";

    assertEquals(source, NrwVariant.rewrite(source, "A.java"));
  }

  @Test
  void refusesAnUnclosedStandardBlock() {
    assertThrows(IllegalStateException.class,
        () -> NrwVariant.rewrite("// nrw-standard-begin\nclass A {}\n", "A.java"));
  }

  @Test
  void refusesAnEndWithoutABeginning() {
    assertThrows(IllegalStateException.class,
        () -> NrwVariant.rewrite("class A {}\n// nrw-standard-end\n", "A.java"));
  }

  /**
   * Builds the NRW library from the real sources and compiles a student's
   * program against it, with the Abitur's List in the default package. This is
   * what a teacher gets from the nrw jar, so it is what has to keep working.
   */
  @Test
  void studentsGetTheAbiturListFromTheNrwBuild(@TempDir Path dir) throws IOException {
    Path sources = dir.resolve("nrw-sources");
    NrwVariant.write(Path.of("src", "main", "java"), sources);
    Path library = dir.resolve("library");
    List<Path> libraryFiles;
    try (Stream<Path> walk = Files.walk(sources)) {
      libraryFiles = walk.filter(p -> p.toString().endsWith(".java")).collect(Collectors.toList());
    }
    assertEquals("", compile(libraryFiles, library, System.getProperty("java.class.path")));

    Path student = dir.resolve("student");
    Files.createDirectories(student);
    Files.writeString(student.resolve("List.java"), String.join("\n",
        "public class List<ContentType> {",
        "  public void append(ContentType pContent) {}",
        "}"), StandardCharsets.UTF_8);
    Files.writeString(student.resolve("Gegner.java"), String.join("\n",
        "import org.openpatch.scratch.*;",
        "public class Gegner extends Sprite {",
        "  public void run() {",
        "    List<Gegner> touching = getTouchingSprites(Gegner.class);",
        "  }",
        "}"), StandardCharsets.UTF_8);
    Files.writeString(student.resolve("MyStage.java"), String.join("\n",
        "import org.openpatch.scratch.*;",
        "public class MyStage extends Stage {",
        "  public void run() {",
        "    List<Gegner> gegner = find(Gegner.class);",
        "    List<Sprite> alle = getAll();",
        "  }",
        "}"), StandardCharsets.UTF_8);
    List<Path> studentFiles = List.of(
        student.resolve("List.java"), student.resolve("Gegner.java"), student.resolve("MyStage.java"));

    assertEquals("", compile(studentFiles, dir.resolve("student-classes"),
        library + File.pathSeparator + System.getProperty("java.class.path")));
  }

  private static String compile(List<Path> files, Path out, String classpath) throws IOException {
    Files.createDirectories(out);
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertTrue(compiler != null, "needs a JDK");
    var diagnostics = new DiagnosticCollector<JavaFileObject>();
    try (StandardJavaFileManager manager =
        compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, StandardCharsets.UTF_8)) {
      var options = List.of("-d", out.toString(), "-nowarn", "-proc:none", "-classpath", classpath);
      boolean ok = compiler
          .getTask(null, manager, diagnostics, options, null,
              manager.getJavaFileObjectsFromPaths(files))
          .call();
      if (ok) {
        return "";
      }
    }
    return diagnostics.getDiagnostics().stream()
        .filter(d -> d.getKind() == Diagnostic.Kind.ERROR)
        .map(d -> d.getSource().getName().replaceAll(".*/", "")
            + ":" + d.getLineNumber() + " " + d.getMessage(Locale.ENGLISH))
        .limit(6)
        .collect(Collectors.joining("\n"));
  }
}
