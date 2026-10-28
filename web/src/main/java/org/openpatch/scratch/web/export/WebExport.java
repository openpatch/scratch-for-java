package org.openpatch.scratch.web.export;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.teavm.diagnostics.Problem;
import org.teavm.tooling.TeaVMProblemRenderer;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.TeaVMToolLog;
import org.teavm.vm.TeaVMOptimizationLevel;

/**
 * A game as a web page: its classes and the library compiled to WebAssembly (Wasm GC) with TeaVM,
 * next to {@code index.html}, the library's images, sounds and fonts, and the game's own files.
 * Runs on a desktop JVM (Studio's export); the folder it writes is served as it is.
 *
 * <p>The class path is the game's classes and the library's jars ({@code scratch},
 * {@code scratch-web}, extensions) but never {@code scratch-desktop}: TeaVM would follow it into
 * LWJGL. TeaVM's own class library is added here.
 *
 * <p>Wasm GC rather than TeaVM's JavaScript: its {@code float} arithmetic is the desktop's (the
 * JavaScript backend computes in double precision, so text would come out differently). Strict
 * mode keeps array bounds and null checks, so a learner's mistake is a Java exception there too.
 */
public final class WebExport {

  /** What to export. */
  public record Request(List<Path> classPath, String mainClass, Path gameFolder, String title, Path output) {}

  /** The compiler found code the browser cannot run (a desktop-only API, a missing JDK method). */
  public static final class CompileException extends IOException {
    private final List<String> problems;

    CompileException(List<String> problems) {
      super("The game cannot run in a browser:\n" + String.join("\n", problems));
      this.problems = List.copyOf(problems);
    }

    public List<String> problems() {
      return this.problems;
    }
  }

  /** Folders of a project that are never part of the page. */
  private static final Set<String> SKIPPED = Set.of("export", "target", "build", "out", "+libs", "bin");

  /** TeaVM's class library and what it needs, found through one class of each jar. */
  private static final String[] TEAVM_CLASSES = {
      "org.teavm.classlib.java.lang.TObject", "org.teavm.runtime.fs.VirtualFileSystem",
      "org.teavm.platform.Platform", "org.teavm.jso.JSObject", "org.teavm.jso.browser.Window",
      "org.teavm.jso.impl.AliasCollector", "org.teavm.interop.Async", "org.teavm.metaprogramming.Action",
      "org.teavm.metaprogramming.impl.ActionImpl", "org.teavm.extension.Autoregistered",
      "com.jcraft.jzlib.Inflater", "org.joda.time.DateTime"};

  private WebExport() {}

  /**
   * {@code java -jar scratch-<version>-web.jar --main <class> --out <folder> [--title <title>]
   * [--game <folder>] <class path entry>...}: the game's classes (and any library jars other than
   * the core and scratch-web, which this jar brings). Prints the page's index.html; on a compile
   * problem prints the problems to stderr and exits with 2.
   */
  public static void main(String[] args) {
    String main = null, title = null;
    Path out = null, game = null;
    List<Path> classPath = new ArrayList<>();
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "--main" -> main = args[++i];
        case "--title" -> title = args[++i];
        case "--out" -> out = Path.of(args[++i]);
        case "--game" -> game = Path.of(args[++i]);
        default -> classPath.add(Path.of(args[i]));
      }
    }
    if (main == null || out == null) {
      System.err.println("usage: --main <class> --out <folder> [--title <title>] [--game <folder>] <class path>...");
      System.exit(1);
    }
    classPath.addAll(libraryClassPath());
    try {
      System.out.println(export(new Request(classPath, main, game, title != null ? title : main, out)));
    } catch (CompileException e) {
      e.problems().forEach(System.err::println);
      System.exit(2);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  /**
   * The core, scratch-web and the extensions that work in a page (all but gamepad), from this program's own
   * class path (in the web jar: the jar itself).
   */
  static List<Path> libraryClassPath() {
    Set<Path> entries = new LinkedHashSet<>();
    for (Class<?> type : new Class<?>[] {org.openpatch.scratch.Stage.class, org.openpatch.scratch.web.WebPlatform.class,
        org.openpatch.scratch.tiled.TiledMap.class, org.openpatch.scratch.save.SaveData.class,
        org.openpatch.scratch.drawing.DrawingExtension.class, org.openpatch.scratch.recorder.GifRecorder.class}) {
      try {
        entries.add(Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()));
      } catch (java.net.URISyntaxException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>(entries);
  }

  /**
   * Writes the page into {@code request.output()}, which must not exist or be empty.
   *
   * @return the page's index.html
   * @throws CompileException when the game uses something a browser cannot run
   */
  public static Path export(Request request) throws IOException {
    Path out = request.output();
    Files.createDirectories(out);
    try (Stream<Path> existing = Files.list(out)) {
      if (existing.findAny().isPresent()) {
        throw new IOException("The folder for the web page is not empty: " + out);
      }
    }
    compile(request, out);
    boolean debug = Boolean.getBoolean("scratch4j.webDebug");
    copyPage(request.title(), debug, out);
    copyLibraryResources(request.classPath(), out);
    if (request.gameFolder() != null) {
      copyGameFiles(request.gameFolder(), out);
    }
    writeManifest(out);
    return out.resolve("index.html");
  }

  private static void compile(Request request, Path out) throws IOException {
    TeaVMTool tool = new TeaVMTool();
    List<String> errors = new ArrayList<>();
    tool.setLog(new TeaVMToolLog() {
      @Override public void info(String text) {}

      @Override public void debug(String text) {}

      @Override public void warning(String text) {}

      @Override public void error(String text) {
        errors.add(text);
      }

      @Override public void info(String text, Throwable e) {}

      @Override public void debug(String text, Throwable e) {}

      @Override public void warning(String text, Throwable e) {}

      @Override public void error(String text, Throwable e) {
        errors.add(text + ": " + e);
      }
    });
    List<File> classPath = new ArrayList<>();
    for (Path entry : request.classPath()) {
      classPath.add(entry.toFile());
    }
    classPath.addAll(teavmClassPath());
    tool.setClassPath(classPath);
    tool.setMainClass(request.mainClass());
    tool.setTargetType(TeaVMTargetType.WEBASSEMBLY_GC);
    tool.setTargetDirectory(out.toFile());
    tool.setTargetFileName("game.wasm");
    tool.setStrict(true);
    // -Dscratch4j.webDebug=true: readable names and debug information, for stack traces in the console
    boolean debug = Boolean.getBoolean("scratch4j.webDebug");
    tool.setObfuscated(!debug);
    tool.setOptimizationLevel(debug ? TeaVMOptimizationLevel.SIMPLE : TeaVMOptimizationLevel.ADVANCED);
    tool.setIncremental(false);
    tool.setDebugInformationGenerated(debug);
    if (debug) {
      tool.setWasmDebugInfoLocation(org.teavm.backend.wasm.WasmDebugInfoLocation.EMBEDDED);
      tool.setWasmDebugInfoLevel(org.teavm.backend.wasm.WasmDebugInfoLevel.FULL);
    }
    try {
      tool.generate();
    } catch (Exception e) {
      throw new IOException("TeaVM could not compile the game: " + e.getMessage(), e);
    }
    List<Problem> severe = tool.getProblemProvider().getSevereProblems();
    if (!severe.isEmpty()) {
      if (errors.isEmpty() && tool.getDependencyInfo() != null) {
        TeaVMProblemRenderer.describeProblems(tool.getDependencyInfo().getCallGraph(), tool.getProblemProvider(),
            tool.getLog());
      }
      throw new CompileException(errors.isEmpty() ? List.of(severe.size() + " problems") : errors);
    }
  }

  /** The jars TeaVM's class library lives in, from this program's own class path. */
  static List<File> teavmClassPath() {
    Set<File> jars = new LinkedHashSet<>();
    for (String name : TEAVM_CLASSES) {
      try {
        var source = Class.forName(name, false, WebExport.class.getClassLoader()).getProtectionDomain().getCodeSource();
        if (source != null) {
          jars.add(Path.of(source.getLocation().toURI()).toFile());
        }
      } catch (ClassNotFoundException | java.net.URISyntaxException e) {
        // an optional part of the class library
      }
    }
    return new ArrayList<>(jars);
  }

  private static void copyPage(String title, boolean debug, Path out) throws IOException {
    String safe = title.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    Files.writeString(out.resolve("index.html"), resource("org/openpatch/scratch/web/index.html")
        .replace("${title}", safe)
        .replace("${loadOptions}", debug ? "{ stackDeobfuscator: { enabled: true } }" : "{}"), StandardCharsets.UTF_8);
    if (debug) {
      // the runtime looks for it next to the game: classes and lines in stack traces
      try (InputStream in = WebExport.class.getClassLoader().getResourceAsStream("org/teavm/backend/wasm/deobfuscator.wasm")) {
        if (in != null) {
          Files.copy(in, out.resolve("game.wasm-deobfuscator.wasm"));
        }
      }
    }
    Files.writeString(out.resolve("s4j.js"), resource("org/openpatch/scratch/web/s4j.js"), StandardCharsets.UTF_8);
    Files.writeString(out.resolve("wasm-gc-runtime.min.js"), resource("org/teavm/backend/wasm/wasm-gc-runtime.min.js"),
        StandardCharsets.UTF_8);
  }

  private static String resource(String name) throws IOException {
    try (InputStream in = WebExport.class.getClassLoader().getResourceAsStream(name)) {
      if (in == null) {
        throw new IOException("Missing from scratch-web: " + name);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  /** The core's images, sounds, fonts and loading screen, by the paths the core reads them. */
  private static void copyLibraryResources(List<Path> classPath, Path out) throws IOException {
    for (Path entry : classPath) {
      if (Files.isDirectory(entry)) {
        if (Files.exists(entry.resolve("org/openpatch/scratch/Stage.class"))) {
          copyResources(entry, out);
          return;
        }
      } else if (Files.isRegularFile(entry) && entry.toString().endsWith(".jar")) {
        try (FileSystem jar = FileSystems.newFileSystem(entry)) {
          Path root = jar.getPath("/");
          if (Files.exists(root.resolve("org/openpatch/scratch/Stage.class"))) {
            copyResources(root, out);
            return;
          }
        }
      }
    }
    throw new IOException("The class path has no Scratch for Java core (scratch-<version>.jar).");
  }

  private static void copyResources(Path root, Path out) throws IOException {
    try (Stream<Path> files = Files.walk(root)) {
      for (Path file : files.filter(Files::isRegularFile).toList()) {
        String path = root.relativize(file).toString().replace('\\', '/');
        boolean wanted = path.startsWith("sounds/") && !path.endsWith(".txt")
            || path.startsWith("images/") && path.endsWith(".png")
            || !path.contains("/") && (path.endsWith(".ttf") || path.equals("loading.png"));
        if (wanted) {
          Path target = out.resolve(path);
          Files.createDirectories(target.getParent());
          Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  /** The game's own files (images, sounds, maps, shaders), not its sources or build output. */
  private static void copyGameFiles(Path game, Path out) throws IOException {
    Path outAbsolute = out.toAbsolutePath().normalize();
    try (Stream<Path> files = Files.walk(game)) {
      for (Path file : files.filter(Files::isRegularFile).toList()) {
        Path relative = game.relativize(file);
        if (file.toAbsolutePath().normalize().startsWith(outAbsolute) || skipped(relative)) {
          continue;
        }
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".java") || name.endsWith(".class") || name.endsWith(".ctxt") || name.equals("package.bluej")) {
          continue;
        }
        Path target = out.resolve(relative.toString().replace('\\', '/'));
        if (Files.exists(target)) {
          continue; // the page's own files and the library's come first
        }
        Files.createDirectories(target.getParent());
        Files.copy(file, target);
      }
    }
  }

  private static boolean skipped(Path relative) {
    for (Path part : relative) {
      String name = part.toString();
      if (name.startsWith(".") || SKIPPED.contains(name)) {
        return true;
      }
    }
    return false;
  }

  /** files.txt: "path TAB size" for every file the game may read, for the page's file system. */
  private static void writeManifest(Path out) throws IOException {
    try (Stream<Path> files = Files.walk(out)) {
      List<String> lines = files.filter(Files::isRegularFile)
          .map(file -> out.relativize(file).toString().replace('\\', '/') + "\t" + size(file))
          .filter(line -> !line.startsWith("game.wasm\t") && !line.startsWith("index.html\t")
              && !line.startsWith("s4j.js\t") && !line.startsWith("wasm-gc-runtime.min.js\t")
              && !line.startsWith("game.wasm-deobfuscator.wasm\t"))
          .sorted().toList();
      Files.writeString(out.resolve("files.txt"), String.join("\n", lines) + "\n", StandardCharsets.UTF_8);
    }
  }

  private static long size(Path file) {
    try {
      return Files.size(file);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
