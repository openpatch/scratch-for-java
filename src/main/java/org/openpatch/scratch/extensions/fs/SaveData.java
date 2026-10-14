package org.openpatch.scratch.extensions.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Saves a game's progress as JSON independently of its installation directory.
 * Data classes should have a public no-argument constructor and public fields.
 * Public fields, including inherited fields, primitives, strings, enums, arrays
 * and nested data classes, form the portable save format. Static and transient
 * fields are excluded. Avoid getters, setters, library objects and object cycles
 * in data intended to move between the desktop and browser implementations.
 *
 * Saves live under the operating-system user's data folder, independently of the
 * game installation. Keep the game ID unchanged across releases. Call save at
 * checkpoints, rather than every frame. This helper assumes one running game
 * instance; it does not synchronize writes from multiple instances or implement
 * schema migrations or Steam-account separation.
 *
 * <p>On Windows, files live in LOCALAPPDATA; on macOS in Library/Application
 * Support; on Linux in XDG_DATA_HOME or .local/share. Each game has a separate
 * saves directory. The browser implementation uses IndexedDB, scoped to the
 * hosting origin and browser profile, with a separate record per game and slot.
 * Browser storage can be cleared by the user or evicted by the browser.
 *
 * <pre>{@code
 * SaveData<Progress> saves = new SaveData<>("org.example.game", "slot1", Progress.class);
 * Progress progress = saves.loadOrDefault(() -> new Progress());
 * progress.coins += 10;
 * saves.save(progress);
 * }</pre>
 *
 * @param <T> the game's data class
 * @example.files SaveDataExample.java
 */
public final class SaveData<T> {
  private static final JsonMapper JSON = JsonMapper.builder()
      .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
      .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
      .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
      .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
      .changeDefaultVisibility(visibility -> visibility.withFieldVisibility(Visibility.PUBLIC_ONLY)
          .withGetterVisibility(Visibility.NONE).withIsGetterVisibility(Visibility.NONE)
          .withSetterVisibility(Visibility.NONE))
      .build();

  private final Path path;
  private final Class<T> type;

  /**
   * Selects a game's save slot without creating a file or opening a database.
   *
   * @param gameId a stable identifier, for example org.openpatch.coincatcher
   * @param slot a slot name, for example slot1, slot2 or slot3
   * @param type the data class to load
   * @throws IllegalArgumentException if a name is unsafe or the root override is relative
   * @example.files SaveDataExample.java
   */
  public SaveData(String gameId, String slot, Class<T> type) {
    checkName(gameId);
    checkName(slot);
    this.type = Objects.requireNonNull(type, "type");
    String override = System.getProperty("scratch4j.saveDataRoot");
    Path root;
    if (override != null && !override.isBlank()) {
      root = Path.of(override);
      if (!root.isAbsolute()) {
        throw new IllegalArgumentException("scratch4j.saveDataRoot must be an absolute path");
      }
    } else {
      root = platformDirectory(System.getProperty("os.name"),
          Path.of(System.getProperty("user.home")), System.getenv("LOCALAPPDATA"),
          System.getenv("XDG_DATA_HOME"));
    }
    path = root.resolve(gameId).resolve("saves").resolve(slot + ".json");
  }

  /**
   * Returns the absolute save file path for desktop tools and Steam Auto-Cloud.
   *
   * @return the desktop file path
   * @desktop-only
   */
  public Path path() {
    return path;
  }

  /**
   * Returns the save's location for diagnostics. On desktop this is a file path;
   * in the browser it identifies an IndexedDB record rather than a file.
   *
   * @return a description of the save location
   */
  public String getLocation() {
    return path.toString();
  }

  /**
   * Returns defaults only when the file is missing. Unreadable, incompatible,
   * empty or corrupt files raise IOException and remain untouched. Defaults
   * stay in memory until the game explicitly calls save.
   *
   * @param defaults creates initial progress for a missing slot; called only when missing
   * @return loaded progress, or the supplied defaults for a missing slot
   * @throws IOException if existing data cannot be read or decoded
   * @example.files SaveDataExample.java
   */
  public T loadOrDefault(Supplier<? extends T> defaults) throws IOException {
    Objects.requireNonNull(defaults, "defaults");
    String contents;
    try {
      contents = Files.readString(path, StandardCharsets.UTF_8);
    } catch (NoSuchFileException missing) {
      return Objects.requireNonNull(defaults.get(), "defaults returned null");
    }
    try {
      T data = JSON.readValue(contents, type);
      if (data == null) {
        throw new IOException("Save data contains null: " + path);
      }
      return data;
    } catch (JacksonException invalid) {
      throw new IOException("Cannot read save data: " + path, invalid);
    }
  }

  /**
   * Serializes before touching the save, flushes a sibling temporary file,
   * and replaces the save through an atomic rename. If the filesystem does
   * not support atomic replacement, saving fails rather than falling back
   * to an unsafe overwrite. Atomic rename is not a backup or an assurance
   * against every possible storage failure.
   * In the browser, the method waits until the IndexedDB transaction commits.
   *
   * @param data the progress to save; must not be null
   * @throws IOException if serialization or writing fails
   * @example.files SaveDataExample.java
   */
  public void save(T data) throws IOException {
    Objects.requireNonNull(data, "data");
    byte[] contents;
    try {
      contents = JSON.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
    } catch (JacksonException invalid) {
      throw new IOException("Cannot serialize save data: " + path, invalid);
    }
    Files.createDirectories(path.getParent());
    Path temporary = Files.createTempFile(path.getParent(), ".save-", ".tmp");
    Throwable failure = null;
    try {
      try (FileChannel file = FileChannel.open(temporary,
          StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
        ByteBuffer buffer = ByteBuffer.wrap(contents);
        while (buffer.hasRemaining()) {
          file.write(buffer);
        }
        file.force(true);
      }
      Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException | RuntimeException | Error error) {
      failure = error;
      throw error;
    } finally {
      try {
        Files.deleteIfExists(temporary);
      } catch (IOException cleanup) {
        if (failure != null) {
          failure.addSuppressed(cleanup);
        } else {
          throw cleanup;
        }
      }
    }
  }

  private static void checkName(String value) {
    Objects.requireNonNull(value, "name");
    if (value.length() > 100 || !value.matches("[A-Za-z0-9][A-Za-z0-9._-]*")
        || value.endsWith(".") || value.contains("..")) {
      throw new IllegalArgumentException("Use a simple game ID or slot name: " + value);
    }
    String stem = value.split("\\.", 2)[0].toUpperCase(Locale.ROOT);
    if (stem.matches("CON|PRN|AUX|NUL|COM[0-9]|LPT[0-9]")) {
      throw new IllegalArgumentException("Name is reserved on Windows: " + value);
    }
  }

  // Kept independent of graphics and Steam, so directory selection is testable.
  static Path platformDirectory(String os, Path home, String localAppData, String xdgDataHome) {
    os = os.toLowerCase(Locale.ROOT);
    if (os.startsWith("windows")) {
      Path configured = absoluteOrNull(localAppData);
      return configured != null ? configured : home.resolve("AppData/Local");
    }
    if (os.startsWith("mac") || os.startsWith("darwin")) {
      return home.resolve("Library/Application Support");
    }
    Path configured = absoluteOrNull(xdgDataHome);
    return configured != null ? configured : home.resolve(".local/share");
  }

  private static Path absoluteOrNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    Path candidate = Path.of(value);
    return candidate.isAbsolute() ? candidate : null;
  }
}
