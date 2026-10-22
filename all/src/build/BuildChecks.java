import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * The build's rules for 7.0, run by all/pom.xml in `verify`:
 *
 * <pre>java all/src/build/BuildChecks.java &lt;repository root&gt; &lt;fat jar&gt;</pre>
 *
 * Exits with 1 and one line per broken rule.
 */
public class BuildChecks {

  static final String[] EXTENSIONS = {"tiled", "save", "recorder", "drawing"};
  static final String[] PLATFORMS = {"linux/x64", "linux/arm64", "macos/x64", "macos/arm64", "windows/x64",
      "windows/arm64"};
  /** Native libraries per platform, relative to org/lwjgl/: core, then one folder per module. */
  static final String[] NATIVE_FOLDERS = {"", "glfw/", "opengl/", "openal/", "stb/", "tinyfd/"};
  /** What 7.0 replaced; none of it may come back through a dependency. */
  static final String[] GONE = {"processing/", "com/jogamp/", "jogamp/", "kotlin/", "com/github/davidmoten/",
      "javazoom/", "org/tritonus/", "com/jcraft/"};

  static final List<String> problems = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    Path root = Path.of(args[0]);
    Path fatJar = Path.of(args[1]);
    List<Path> shipped = new ArrayList<>(List.of(root.resolve("core/target/classes")));
    for (String e : EXTENSIONS) {
      shipped.add(root.resolve("extensions/" + e + "/target/classes"));
    }
    for (Path classes : shipped) {
      noJavaDesktop(classes);
    }
    for (String e : EXTENSIONS) {
      Path classes = root.resolve("extensions/" + e + "/target/classes");
      noInternalImports(classes, e);
      noForbiddenCalls(classes, e);
    }
    fatJar(fatJar);
    if (problems.isEmpty()) {
      System.out.println("build checks: all rules hold");
    } else {
      problems.forEach(p -> System.err.println("build checks: " + p));
      System.exit(1);
    }
  }

  /** The core and the first-party extensions run without java.desktop (decision log). */
  static void noJavaDesktop(Path classes) throws IOException {
    // a module may only use what it requires (javac sees to that), so its descriptor says it all
    Path descriptor = classes.resolve("module-info.class");
    if (Files.exists(descriptor)) {
      String module = run("javap", descriptor.toString());
      if (module.contains("java.desktop")) {
        problems.add(classes + ": module-info requires java.desktop:\n" + module);
      }
    }
    // and the classes themselves; jdeps would try to resolve the descriptor's modules, so it
    // looks at a copy without it
    Path copy = Files.createTempDirectory("buildchecks");
    try (var files = Files.walk(classes)) {
      for (Path f : (Iterable<Path>) files::iterator) {
        if (Files.isRegularFile(f) && f.toString().endsWith(".class") && !f.equals(descriptor)) {
          Path target = copy.resolve(classes.relativize(f).toString());
          Files.createDirectories(target.getParent());
          Files.copy(f, target);
        }
      }
    }
    try {
      String out = run("jdeps", "--list-deps", "--ignore-missing-deps", "--multi-release", "25", copy.toString());
      if (out.contains("java.desktop")) {
        problems.add(classes + " uses java.desktop:\n" + out);
      }
    } finally {
      try (var made = Files.walk(copy)) {
        for (Path f : made.sorted(java.util.Comparator.reverseOrder()).toList()) {
          Files.delete(f);
        }
      }
    }
  }

  /** Extensions use only public API: nothing from org.openpatch.scratch.internal. */
  static void noInternalImports(Path classes, String extension) throws IOException {
    for (Path c : classFiles(classes)) {
      // class names and descriptors sit in the constant pool as plain UTF-8
      String pool = new String(Files.readAllBytes(c), StandardCharsets.ISO_8859_1);
      if (pool.contains("org/openpatch/scratch/internal/")) {
        problems.add("extension " + extension + ": " + classes.relativize(c)
            + " uses org.openpatch.scratch.internal");
      }
    }
  }

  /**
   * NRW: find, getAll and getTouchingSprites return another type in the NRW jar, so a call
   * compiled against the standard jar fails there. GLFW: extensions may poll, never set
   * callbacks, which would replace the core's.
   */
  static void noForbiddenCalls(Path classes, String extension) throws IOException {
    Pattern nrw = Pattern.compile("Method org/openpatch/scratch/(Stage|Sprite)\\.(find|getAll|getTouchingSprites):");
    Pattern callback = Pattern.compile("Method org/lwjgl/glfw/GLFW\\.(glfwSet\\w+Callback)");
    for (Path c : classFiles(classes)) {
      String code = run("javap", "-c", "-p", c.toString());
      Matcher m = nrw.matcher(code);
      if (m.find()) {
        problems.add("extension " + extension + ": " + classes.relativize(c) + " calls " + m.group(1) + "."
            + m.group(2) + ", whose type differs in the NRW jar");
      }
      m = callback.matcher(code);
      if (m.find()) {
        problems.add("extension " + extension + ": " + classes.relativize(c) + " sets a GLFW callback ("
            + m.group(1) + "); extensions may only poll");
      }
    }
  }

  static void fatJar(Path path) throws IOException {
    if (!Files.exists(path)) {
      problems.add("no fat jar at " + path);
      return;
    }
    try (JarFile jar = new JarFile(path.toFile())) {
      var attributes = jar.getManifest().getMainAttributes();
      // LWJGL's FFM memory backend is in META-INF/versions/25
      if (!"true".equals(attributes.getValue("Multi-Release"))) {
        problems.add("fat jar: the manifest lacks Multi-Release: true");
      }
      if (!"ALL-UNNAMED".equals(attributes.getValue("Enable-Native-Access"))) {
        problems.add("fat jar: the manifest lacks Enable-Native-Access: ALL-UNNAMED");
      }
      List<String> names = jar.stream().map(e -> e.getName()).toList();
      names.stream().filter(n -> n.endsWith("module-info.class"))
          .forEach(n -> problems.add("fat jar: " + n + " would make the jar claim to be that module"));
      for (String gone : GONE) {
        if (names.stream().anyMatch(n -> n.startsWith(gone))) {
          problems.add("fat jar: still contains " + gone);
        }
      }
      if (jar.getEntry("THIRD-PARTY-NOTICES.txt") == null) {
        problems.add("fat jar: THIRD-PARTY-NOTICES.txt is missing");
      }
      var services = jar.getEntry("META-INF/services/org.openpatch.scratch.extension.Extension");
      String registered = services == null ? "" : new String(jar.getInputStream(services).readAllBytes(),
          StandardCharsets.UTF_8);
      for (String e : EXTENSIONS) {
        if (!registered.contains("org.openpatch.scratch." + e + ".")) {
          problems.add("fat jar: extension " + e + " is not registered in META-INF/services");
        }
      }
      for (String platform : PLATFORMS) {
        for (String folder : NATIVE_FOLDERS) {
          String prefix = platform + "/org/lwjgl/" + folder;
          boolean found = names.stream().anyMatch(n -> n.startsWith(prefix)
              && n.indexOf('/', prefix.length()) < 0 && n.matches(".*\\.(so|dylib|dll)"));
          if (!found) {
            problems.add("fat jar: no native library in " + prefix);
          }
        }
      }
    }
  }

  static List<Path> classFiles(Path classes) throws IOException {
    if (!Files.isDirectory(classes)) {
      problems.add(classes + " does not exist");
      return List.of();
    }
    try (Stream<Path> files = Files.walk(classes)) {
      return files.filter(f -> f.toString().endsWith(".class")).toList();
    }
  }

  static String run(String tool, String... args) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (PrintStream print = new PrintStream(out, true, StandardCharsets.UTF_8)) {
      int code = ToolProvider.findFirst(tool).orElseThrow().run(print, print, args);
      if (code != 0) {
        problems.add(tool + " " + String.join(" ", args) + " failed:\n" + out.toString(StandardCharsets.UTF_8));
      }
    }
    return out.toString(StandardCharsets.UTF_8);
  }
}
