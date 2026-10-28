package org.openpatch.scratch.internal.platform;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The class path of a desktop JVM, for {@link Platform#classpathFolder},
 * {@link Platform#listResources} and {@link Platform#codeLocation}: folders in jars and classes
 * directories. A browser has no class path; only desktop platforms call this.
 */
public final class JvmResources {

  private JvmResources() {}

  /**
   * A folder on the classpath (also inside a jar, whether or not the jar has an entry for the
   * folder itself), or null.
   */
  public static Path classpathFolder(String folder) {
    var resource = Thread.currentThread().getContextClassLoader().getResource(folder);
    if (resource != null) {
      try {
        Path onClasspath = folder(resource.toURI());
        if (onClasspath != null && Files.isDirectory(onClasspath)) {
          return onClasspath;
        }
      } catch (URISyntaxException | IOException | RuntimeException e) {
        // look into the jars instead
      }
    }
    return folderInsideAJar(folder);
  }

  /** The files under a folder of the library's own resources, relative to it. */
  public static List<String> listResources(String folder) {
    List<String> out = new ArrayList<>();
    try {
      URL root = resourceRoot(folder);
      if (root != null) {
        URI uri = root.toURI();
        if ("jar".equals(uri.getScheme())) {
          // inside a jar the resources are entries in a zip file system
          try (FileSystem fs = FileSystems.newFileSystem(uri, Map.of())) {
            collect(fs.getPath("/" + folder), out);
          }
        } else {
          collect(Path.of(uri), out);
        }
      }
    } catch (Exception e) {
      // unreadable: nothing listed
    }
    return out;
  }

  /** The jar or folder a class was loaded from, or null. */
  public static Object codeLocation(Class<?> type) {
    var domain = type.getProtectionDomain();
    return domain == null || domain.getCodeSource() == null ? null : domain.getCodeSource().getLocation();
  }

  private static URL resourceRoot(String folder) {
    ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    if (contextLoader != null) {
      URL url = contextLoader.getResource(folder);
      if (url != null) {
        return url;
      }
    }
    URL url = Platform.class.getClassLoader().getResource(folder);
    // on the module path the folder is a package of the core module: only its classes can see it
    return url != null ? url : Platform.class.getResource("/" + folder);
  }

  private static void collect(Path root, List<String> into) throws IOException {
    try (Stream<Path> walk = Files.walk(root)) {
      walk.filter(Files::isRegularFile)
          .forEach(file -> into.add(root.relativize(file).toString().replace('\\', '/')));
    }
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

  private static Path folder(URI uri) throws IOException {
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
}
