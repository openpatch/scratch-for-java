package org.openpatch.scratch.extension;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * An extension: found through {@link java.util.ServiceLoader} (a line in
 * {@code META-INF/services/org.openpatch.scratch.extension.Extension}) or registered with
 * {@code Window.use(...)}, installed once when the window is created. Learner code never sees
 * it.
 *
 * <p>The metadata lives in code, not in a resource file, because the fat jar shades every
 * first-party extension into one jar, where a fixed resource path would survive only once.
 */
public interface Extension {

  /** A short unique id such as {@code "tiled"}, used in messages. */
  String id();

  /** A name for people, such as {@code "Tiled maps"}. */
  String name();

  /** The extension's own version. */
  String version();

  /**
   * The core versions this extension works with: {@code "7"} means any 7.x, {@code "7.1"} 7.1
   * or newer, {@code "7.1.2"} 7.1.2 or newer, always below the next major version.
   */
  default String requires() {
    return "7";
  }

  /** Adds the extension's hooks. Called once, on the window thread, before the first stage. */
  default void install(ExtensionContext ctx) {}

  /** The version of the core library, e.g. {@code "7.0.0"}. */
  static String coreVersion() {
    return CoreVersion.VALUE;
  }
}

/**
 * Read once from the version file Maven fills in. This core is the version 7 extension API; until
 * the release workflow sets 7.x the pom still carries the last release's number (6.5.0 on the v7
 * branch), which would make every extension that requires 7 refuse to run. Such a build reports
 * 7.0.0; a 7.x or later version is reported as it is.
 */
final class CoreVersion {
  static final int API_MAJOR = 7;
  static final String VALUE = effective(read());

  static String effective(String version) {
    try {
      int major = Integer.parseInt(version.split("[.-]")[0]);
      return major < API_MAJOR ? API_MAJOR + ".0.0" : version;
    } catch (NumberFormatException e) {
      return version;
    }
  }

  private CoreVersion() {}

  private static String read() {
    try (InputStream in = Extension.class.getResourceAsStream(
        "/org/openpatch/scratch/internal/version.properties")) {
      if (in == null) {
        return "0";
      }
      Properties p = new Properties();
      p.load(in);
      return p.getProperty("version", "0");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
