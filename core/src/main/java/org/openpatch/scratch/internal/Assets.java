package org.openpatch.scratch.internal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;
import org.openpatch.scratch.internal.image.Image;
import org.openpatch.scratch.internal.image.Images;
import org.openpatch.scratch.internal.text.Font;

/**
 * Preloads an assets folder while the loading screen shows:
 * the folder given to the window, else a folder named {@code assets} if the project has one.
 *
 * <p>A worker thread decodes every image, font and sound into the caches a sprite's
 * {@code addCostume} or {@code addSound} reads, by the path the sprite would write
 * ({@code assets/cat.png}); the window thread then uploads the decoded images a few milliseconds
 * per frame ({@link #nextUpload()}), so the screen keeps moving. Progress counts both steps.
 */
public final class Assets {

  /** The folder that is preloaded when the window is given none. */
  public static final String DEFAULT_FOLDER = "assets";

  /** The program's preloader, for {@link #noteLoadedLate}. */
  private static volatile Assets active;

  private final String folder;
  private final boolean named;
  private final ConcurrentLinkedQueue<Image> uploads = new ConcurrentLinkedQueue<>();
  private volatile boolean listed;
  private volatile boolean decoded;
  private volatile int total;
  private volatile int done;
  private volatile String current = "";

  private Assets(String folder) {
    this.named = folder != null;
    this.folder = folder != null ? folder : DEFAULT_FOLDER;
  }

  /** Starts preloading on a daemon thread; nothing to do when there is no such folder. */
  public static Assets start(String folder) {
    Assets assets = new Assets(folder);
    active = assets;
    Thread worker = new Thread(assets::load, "scratch4j-assets");
    worker.setDaemon(true);
    worker.start();
    return assets;
  }

  /**
   * Notes a file loaded while the program was already running: it was not ready when first used,
   * so the game may have stuttered while it loaded. Debug mode says which file, and how to load
   * it while the loading screen shows.
   *
   * @param type the kind of file, e.g. "image"
   * @param path the path it was loaded by
   */
  public static void noteLoadedLate(String type, String path) {
    Assets assets = active;
    Game game = Game.currentOrNull();
    if (assets == null || !assets.isDone() || game == null || !game.isDebug()) {
      return;
    }
    String prefix = assets.folder.replace('\\', '/').replaceAll("/+$", "") + "/";
    String hint = path.replace('\\', '/').startsWith(prefix)
        ? "the folder '" + assets.folder + "' could not be loaded in advance"
        : "put it into '" + assets.folder + "' to load it while the loading screen shows";
    System.out.println("[Debug] Loaded " + type + " late: " + path + " (" + hint + ")");
  }

  /** Whether every file is decoded and every image uploaded. */
  public boolean isDone() {
    return this.listed && this.decoded && this.uploads.isEmpty();
  }

  /** 0 … 1; images count twice (decoding, uploading). */
  public double progress() {
    if (!this.listed) {
      return 0;
    }
    int total = this.total;
    return total == 0 ? 1 : Math.min(1, (this.done - this.uploads.size()) / (double) total);
  }

  /** "Image: assets/cat.png", or what the loader is doing. */
  public String current() {
    return this.current;
  }

  /** Window thread: the next decoded image to upload, or null. */
  public Image nextUpload() {
    return this.uploads.poll();
  }

  private void load() {
    try {
      Font.defaultFont();
      Path root = findFolder(this.folder);
      if (root == null) {
        if (this.named) {
          reportMissingFolder(this.folder);
        }
        return;
      }
      this.current = "Finding files...";
      List<String> all = list(this.folder, root);
      List<String> images = new ArrayList<>(), fonts = new ArrayList<>(), sounds = new ArrayList<>();
      for (String file : all) {
        if (hasExtension(file, ".png", ".jpg", ".jpeg", ".gif", ".bmp")) {
          images.add(file);
        } else if (hasExtension(file, ".ttf", ".otf")) {
          fonts.add(file);
        } else if (hasExtension(file, ".wav", ".ogg")) {
          sounds.add(file);
        }
      }
      this.total = images.size() * 2 + fonts.size() + sounds.size();
      this.listed = true;
      for (String file : images) {
        this.step("Image", file, () -> this.uploads.add(Images.load(file)), 2);
      }
      for (String file : fonts) {
        this.step("Font", file, () -> Font.load(file), 1);
      }
      for (String file : sounds) {
        this.step("Sound", file, () -> SoundBank.preload(file), 1);
      }
    } catch (IOException | RuntimeException e) {
      System.err.println("Scratch for Java: could not load the assets folder '" + this.folder + "': " + e);
    } finally {
      // whatever went wrong, the project must not wait on its loading screen
      this.listed = true;
      this.decoded = true;
      this.done = this.total;
    }
  }

  /** One file; a file that cannot be loaded is reported when the program uses it, not here. */
  private void step(String type, String file, Runnable load, int weight) {
    this.current = type + ": " + file;
    try {
      load.run();
      this.done += weight;
    } catch (RuntimeException e) {
      this.done += weight;
    }
  }

  /**
   * Finds an assets folder relative to the working directory, else on the classpath (also inside
   * a jar, whether or not the jar has an entry for the folder itself).
   *
   * @return the folder, or null if there is none
   */
  static Path findFolder(String folder) {
    folder = folder.replaceFirst("^~", java.util.regex.Matcher.quoteReplacement(System.getProperty("user.home")));
    Path onDisk = Path.of(folder);
    if (Files.isDirectory(onDisk)) {
      return onDisk;
    }
    if (onDisk.isAbsolute()) {
      return null;
    }
    var resource = Thread.currentThread().getContextClassLoader().getResource(folder);
    if (resource != null) {
      try {
        Path onClasspath = classpathFolder(resource.toURI());
        if (onClasspath != null && Files.isDirectory(onClasspath)) {
          return onClasspath;
        }
      } catch (URISyntaxException | IOException | RuntimeException e) {
        // look into the jars instead
      }
    }
    return folderInsideAJar(folder);
  }

  /**
   * Looks for a folder inside the jars on the classpath. A jar only has the folder as a resource
   * of its own when it was packed with an entry for it, which not every tool writes.
   */
  private static Path folderInsideAJar(String folder) {
    try {
      var manifests = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
      while (manifests.hasMoreElements()) {
        String manifest = manifests.nextElement().toString();
        if (!manifest.startsWith("jar:")) {
          continue;
        }
        URI jar = URI.create(manifest.substring(0, manifest.indexOf("!/") + 2));
        FileSystem opened = null;
        FileSystem fs;
        try {
          fs = FileSystems.getFileSystem(jar);
        } catch (FileSystemNotFoundException e) {
          fs = opened = FileSystems.newFileSystem(jar, Map.of());
        }
        Path candidate = fs.getPath(folder);
        if (Files.isDirectory(candidate)) {
          return candidate; // left open: the files are read from it afterwards
        }
        if (opened != null) {
          opened.close();
        }
      }
    } catch (IOException | RuntimeException e) {
      // no such folder in any jar
    }
    return null;
  }

  private static Path classpathFolder(URI uri) throws IOException {
    if ("file".equals(uri.getScheme())) {
      return Path.of(uri);
    }
    if (!"jar".equals(uri.getScheme())) {
      return null;
    }
    try {
      return Path.of(uri);
    } catch (FileSystemNotFoundException e) {
      FileSystems.newFileSystem(uri, Map.of()); // left open: the files are read from it afterwards
      return Path.of(uri);
    }
  }

  /**
   * The files of an assets folder by the paths a sprite would use for them: the folder as it was
   * written, then the path inside it, sorted.
   */
  static List<String> list(String folder, Path root) throws IOException {
    String prefix = folder.replace('\\', '/').replaceAll("/+$", "");
    try (Stream<Path> files = Files.walk(root)) {
      return files.filter(Files::isRegularFile)
          .map(f -> prefix + "/" + root.relativize(f).toString().replace('\\', '/'))
          .sorted()
          .toList();
    }
  }

  private static boolean hasExtension(String file, String... extensions) {
    String lower = file.toLowerCase(Locale.ROOT);
    for (String extension : extensions) {
      if (lower.endsWith(extension)) {
        return true;
      }
    }
    return false;
  }

  private static void reportMissingFolder(String folder) {
    System.err.println("\n==============================================");
    System.err.println("WARNING: Assets folder not found!");
    System.err.println("==============================================");
    System.err.println("You wrote:      " + folder);
    System.err.println("Java looked at: " + Path.of(folder).toAbsolutePath());
    System.err.println("\nNothing is loaded in advance, so each image and sound");
    System.err.println("is loaded the first time it is used.");
    System.err.println("\nTip: Check the folder name for typos. A folder named");
    System.err.println("     'assets' is found without naming it at all:");
    System.err.println("       new Window(480, 360);");
    System.err.println("==============================================\n");
  }
}
