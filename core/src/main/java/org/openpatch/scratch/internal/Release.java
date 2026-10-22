package org.openpatch.scratch.internal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * A released game: started with
 * {@code -Dscratch4j.release=<GameName>} (Studio's export sets it), the program copies what it
 * prints to {@code <per-user data>/<GameName>/logs/latest.log} (the run before kept as
 * {@code previous.log}) and, when it crashes, says so in a native message box naming that log
 * before it ends. Without the property nothing changes: in class, the console shows the error and
 * the window stays open.
 */
public final class Release {

  private static final String NAME = System.getProperty("scratch4j.release");
  private static volatile Path log;

  private Release() {}

  /** Whether this run is a released game. */
  public static boolean isOn() {
    return NAME != null && !NAME.isBlank();
  }

  /** Once, before anything is printed: stdout and stderr go to the log file as well. */
  public static synchronized void start() {
    if (!isOn() || log != null) {
      return;
    }
    try {
      Path folder = dataFolder(System.getProperty("os.name"), Path.of(System.getProperty("user.home")),
          System.getenv("LOCALAPPDATA"), System.getenv("XDG_DATA_HOME")).resolve(safe(NAME)).resolve("logs");
      Files.createDirectories(folder);
      Path latest = folder.resolve("latest.log");
      if (Files.exists(latest)) {
        Files.move(latest, folder.resolve("previous.log"), StandardCopyOption.REPLACE_EXISTING);
      }
      OutputStream file = new FileOutputStream(latest.toFile());
      System.setOut(new PrintStream(new Both(System.out, file), true, StandardCharsets.UTF_8));
      System.setErr(new PrintStream(new Both(System.err, file), true, StandardCharsets.UTF_8));
      log = latest;
      System.out.println(NAME + " (Scratch for Java " + Release.class.getPackage().getImplementationVersion()
          + ", Java " + System.getProperty("java.version") + ", " + System.getProperty("os.name") + " "
          + System.getProperty("os.arch") + ")");
    } catch (IOException | RuntimeException e) {
      System.err.println("Scratch for Java: could not open the log file: " + e);
    }
  }

  /**
   * In a released game: an error box saying the game crashed and where its log is (blocks until
   * closed). Does nothing otherwise.
   *
   * @param what one sentence on what went wrong
   */
  public static void crashed(String what) {
    if (!isOn()) {
      return;
    }
    String message = what + "\n\n" + (log != null ? "The log is at\n" + log : "There is no log file.");
    System.err.println(message);
    try {
      TinyFileDialogs.tinyfd_messageBox(boxText(NAME), wrap(boxText(message), 70), "ok", "error", 1);
    } catch (Throwable t) {
      System.err.println("Scratch for Java: could not show the error box: " + t);
    }
  }

  /**
   * Text tinyfd accepts: it refuses ' " ` and $ before a letter (it hands the text to a shell),
   * showing "INVALID MESSAGE WITH QUOTES" instead; and zenity reads it as Pango markup, falling
   * back to its own "An error has occurred" for {@code <local1>}. Look-alikes read the same
   * everywhere, where escaping would show {@code &lt;} on Windows and macOS.
   */
  static String boxText(String text) {
    return text.replace('"', '\u201d').replace('\'', '\u2019').replace('`', '\u2019').replace('$', '.')
        .replace('<', '\u2039').replace('>', '\u203a').replace('&', '+');
  }

  /** Lines of at most {@code width} characters (tinyfd does not wrap), broken after a space or a slash. */
  static String wrap(String text, int width) {
    StringBuilder out = new StringBuilder();
    for (String line : text.split("\n", -1)) {
      while (line.length() > width) {
        int at = Math.max(line.lastIndexOf(' ', width), Math.max(line.lastIndexOf('/', width - 1),
            line.lastIndexOf('\\', width - 1)));
        int cut = at > 0 ? at + 1 : width;
        out.append(line.substring(0, cut).stripTrailing()).append('\n');
        line = line.substring(cut).stripLeading();
      }
      out.append(line).append('\n');
    }
    return out.substring(0, out.length() - 1);
  }

  /** Where per-user data lives: as the save extension's {@code SaveData}, kept the same by hand. */
  static Path dataFolder(String os, Path home, String localAppData, String xdgDataHome) {
    os = os.toLowerCase(Locale.ROOT);
    if (os.startsWith("windows")) {
      return absolute(localAppData) != null ? absolute(localAppData) : home.resolve("AppData/Local");
    }
    if (os.startsWith("mac") || os.startsWith("darwin")) {
      return home.resolve("Library/Application Support");
    }
    return absolute(xdgDataHome) != null ? absolute(xdgDataHome) : home.resolve(".local/share");
  }

  private static Path absolute(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    Path path = Path.of(value);
    return path.isAbsolute() ? path : null;
  }

  /** A game name as a folder name: letters, digits, dot, dash, underscore. */
  static String safe(String name) {
    String folder = name.strip().replaceAll("[^A-Za-z0-9._-]", "_").replaceAll("^\\.+", "_");
    return folder.isEmpty() ? "game" : folder;
  }

  /** Writes to the console and the file; a full disk does not stop the game. */
  private static final class Both extends OutputStream {
    private final OutputStream console;
    private final OutputStream file;
    private boolean fileFailed;

    Both(OutputStream console, OutputStream file) {
      this.console = console;
      this.file = file;
    }

    @Override
    public synchronized void write(int b) throws IOException {
      this.console.write(b);
      if (!this.fileFailed) {
        try {
          this.file.write(b);
        } catch (IOException e) {
          this.fileFailed = true;
        }
      }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
      this.console.write(b, off, len);
      if (!this.fileFailed) {
        try {
          this.file.write(b, off, len);
        } catch (IOException e) {
          this.fileFailed = true;
        }
      }
    }

    @Override
    public synchronized void flush() throws IOException {
      this.console.flush();
      if (!this.fileFailed) {
        try {
          this.file.flush();
        } catch (IOException e) {
          this.fileFailed = true;
        }
      }
    }
  }
}
