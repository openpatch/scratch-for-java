package org.openpatch.scratch.save;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Saves objects as JSON files and loads them again: their public fields and bean properties
 * (getters and setters), with lists, maps, arrays, enums and nested objects.
 *
 * <p>
 * Usage examples:
 *
 * <pre>{@code
 * // Save an object as JSON
 * File.save("/path/to/file.json", myObject);
 *
 * // Load an object from JSON
 * MyClass obj = File.load("/path/to/file.json", MyClass.class);
 * }</pre>
 *
 * <p>
 * Note: The file paths can be absolute or relative. A relative path is relative to the working
 * directory; a file that is only on the classpath (inside a jar) is read from there.
 *
 * <p>
 * For save games, {@link SaveData} is safer: it keeps the last save when writing fails and
 * stores in the player's own folder.
 * @desktop-only
 */
public class File {

  /** The object as JSON: public fields and bean properties. */
  private static String format(Object obj) throws Json.JsonException {
    return Json.write(obj, true);
  }

  /**
   * Saves the given object as a JSON file at the specified path.
   *
   * @param <T>  the type of the object to be saved
   * @param path the path where the object should be saved
   * @param obj  the object to be saved
   */
  public static <T> void save(String path, T obj) {
    write(path, obj);
  }

  /** Writes the JSON, creating missing folders; reports a failure on the console. */
  private static <T> void write(String path, T obj) {
    path = resolve(path);
    java.io.File file = new java.io.File(path);

    // Ensure parent directories exist
    java.io.File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
      parent.mkdirs();
    }

    try {
      java.nio.file.Files.write(java.nio.file.Path.of(path), format(obj).getBytes(StandardCharsets.UTF_8));
    } catch (IOException | Json.JsonException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads an object of the specified class from a file at the given path.
   *
   * @param <T>  the type of the object to be loaded
   * @param path the path to the file from which the object is to be loaded
   * @param cls  the class of the object to be loaded
   * @return the loaded object of the specified class
   */
  public static <T> T load(String path, Class<T> cls) {
    return load(path, cls, false);
  }

  /**
   * Loads an object of the specified class from a file at the given path, or
   * creates a new one if the file does not exist.
   *
   * @param <T>  the type of the object to be loaded
   * @param path the path to the file from which the object is to be loaded
   * @param cls  the class of the object to be loaded
   * @return the loaded object of the specified class, or a new instance if the
   *         file does not exist
   */
  public static <T> T loadOrCreate(String path, Class<T> cls) {
    return load(path, cls, true);
  }

  private static <T> T load(String path, Class<T> cls, boolean createIfMissing) {
    String originalPath = path;
    path = resolve(path);
    java.io.File file = new java.io.File(path);

    // Create file if missing and flag is set
    if (!file.exists() && createIfMissing) {
      try {
        file.getParentFile().mkdirs(); // ensure parent directories exist
        file.createNewFile(); // creates empty file
        // Optionally: write default content so deserialization won't fail
        java.nio.file.Files.write(java.nio.file.Path.of(path),
            format(cls.getDeclaredConstructor().newInstance()).getBytes(StandardCharsets.UTF_8));
      } catch (Exception e) {
        System.err.println("\n==============================================");
        System.err.println("ERROR: Could not create data file!");
        System.err.println("==============================================");
        System.err.println("Path: " + originalPath);
        System.err.println("\nPossible reasons:");
        System.err.println("  1. No write permission for this location");
        System.err.println("  2. Invalid path or filename");
        System.err.println("  3. Parent directory does not exist");
        System.err.println("\nTip: Check your file path and permissions.");
        System.err.println("==============================================\n");
        return null;
      }
    }

    if (!file.exists()) {
      System.err.println("\n==============================================");
      System.err.println("ERROR: Could not load data file!");
      System.err.println("==============================================");
      System.err.println("Path: " + originalPath);
      System.err.println("\nPossible reasons:");
      System.err.println("  1. The file does not exist at this location");
      System.err.println("  2. The file path is incorrect (check spelling)");
      System.err.println("\nTip: Use loadOrCreate()");
      System.err.println("     to automatically create missing files.");
      System.err.println("==============================================\n");
      return null;
    }

    try {
      String text = new String(java.nio.file.Files.readAllBytes(java.nio.file.Path.of(path)), StandardCharsets.UTF_8);
      return Json.read(text, cls, false, true);
    } catch (Exception e) {
      System.err.println("\n==============================================");
      System.err.println("ERROR: Could not parse data file!");
      System.err.println("==============================================");
      System.err.println("Path: " + originalPath);
            System.err.println("\nPossible reasons:");
      System.err.println("  1. The file is not valid JSON");
      System.err.println("  2. The file structure does not match the class");
      System.err.println("  3. The file is corrupted or empty");
      System.err.println("\nDetails: " + e.getMessage());
      System.err.println("==============================================\n");
      return null;
    }
  }

  /**
   * Where a path points: {@code ~} is the home folder, an absolute path stays, a file that exists
   * on the classpath as a plain file is used there, anything else is relative to the working
   * directory.
   */
  static String resolve(String path) {
    if (path.startsWith("~")) {
      path = System.getProperty("user.home") + path.substring(1);
    }
    java.nio.file.Path fsPath = java.nio.file.Path.of(path);
    if (fsPath.isAbsolute() || java.nio.file.Files.exists(fsPath)) {
      return fsPath.toString();
    }
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try (var in = loader == null ? null : loader.getResourceAsStream(path.replace('\\', '/'))) {
      if (in != null) {
        // inside a jar (an exported game): read from a temporary copy
        java.nio.file.Path temp = java.nio.file.Files.createTempFile("resource-", "-" + fsPath.getFileName());
        temp.toFile().deleteOnExit();
        java.nio.file.Files.copy(in, temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return temp.toString();
      }
    } catch (java.io.IOException e) {
      throw new java.io.UncheckedIOException("Could not read " + path + " from the jar", e);
    }
    return fsPath.toAbsolutePath().toString();
  }
}
