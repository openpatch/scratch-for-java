package org.openpatch.scratch.internal;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Registry for the sound effects that ship inside the Scratch for Java jar.
 *
 * <p>
 * The bundled sounds come from <a href="https://kenney.nl">kenney.nl</a> and
 * are released under CC0, so they can be used for any purpose without
 * attribution. They can be addressed by plain name:
 *
 * <pre>{@code
 * sprite.addSound("footstep_carpet_000");
 * }</pre>
 *
 * <p>
 * Every bundled sound has a unique name, so no folder has to be given. Lookup
 * ignores case.
 */
public final class BuiltinSounds {

  private BuiltinSounds() {
  }

  private static final String BASE = "sounds";

  private static Map<String, String> sounds;

  private static synchronized Map<String, String> getSounds() {
    if (sounds != null) {
      return sounds;
    }

    Map<String, String> loaded = new LinkedHashMap<>();
    try {
      URL root = resourceRoot();
      if (root != null) {
        URI uri = root.toURI();
        if ("jar".equals(uri.getScheme())) {
          // Inside a jar the resources are entries in a zip file system.
          try (FileSystem fs = FileSystems.newFileSystem(uri, Map.of())) {
            collect(fs.getPath("/" + BASE), loaded);
          }
        } else {
          collect(Path.of(uri), loaded);
        }
      }
    } catch (Exception e) {
      // Missing or unreadable bundled sounds must never stop a student's
      // program from starting - it only means they are unavailable.
    }

    sounds = loaded;
    return sounds;
  }

  private static URL resourceRoot() {
    ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    if (contextLoader != null) {
      URL url = contextLoader.getResource(BASE);
      if (url != null) {
        return url;
      }
    }
    return BuiltinSounds.class.getClassLoader().getResource(BASE);
  }

  private static void collect(Path root, Map<String, String> into) throws java.io.IOException {
    List<Path> files = new ArrayList<>();
    try (Stream<Path> walk = Files.walk(root)) {
      walk.filter(Files::isRegularFile).filter(BuiltinSounds::isSound).forEach(files::add);
    }
    // Walk order differs between a directory and a jar, so sort for a stable
    // registry.
    Collections.sort(files, (a, b) -> a.toString().compareTo(b.toString()));

    for (Path file : files) {
      String name = Suggestions.stripExtension(file.getFileName().toString());
      // Store the classloader-style path, which is what Applet.getPath expects.
      String relative = root.relativize(file).toString().replace('\\', '/');
      into.putIfAbsent(Suggestions.normalise(name), BASE + "/" + relative);
    }
  }

  private static boolean isSound(Path path) {
    String name = Suggestions.normalise(path.getFileName().toString());
    return name.endsWith(".ogg") || name.endsWith(".wav")
        || name.endsWith(".aiff") || name.endsWith(".au");
  }

  /**
   * Looks up a bundled sound.
   *
   * @param name the name of a bundled sound, without folder or extension
   * @return the resource path to hand to the sound loader, or null if unknown
   */
  public static String get(String name) {
    if (name == null) {
      return null;
    }
    return getSounds().get(Suggestions.normalise(name));
  }

  /**
   * Checks whether a bundled sound with this name exists.
   *
   * @param name the name to look for
   * @return true if it exists
   */
  public static boolean contains(String name) {
    return get(name) != null;
  }

  /**
   * Returns every bundled sound name.
   *
   * @return all available names
   */
  public static List<String> getNames() {
    List<String> names = new ArrayList<>();
    for (String path : getSounds().values()) {
      String file = path.substring(path.lastIndexOf('/') + 1);
      names.add(Suggestions.stripExtension(file));
    }
    return names;
  }

  /**
   * Finds bundled sound names close to what was typed, so a wrong name can be
   * answered with "did you mean ...?".
   *
   * @param name  the name that could not be found
   * @param limit how many suggestions to return at most
   * @return the closest matching names, best first
   */
  public static List<String> suggest(String name, int limit) {
    return Suggestions.closestTo(getNames(), name, limit);
  }
}
